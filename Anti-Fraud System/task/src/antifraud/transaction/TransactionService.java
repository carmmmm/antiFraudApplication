package antifraud.transaction;

import antifraud.databaseentities.CardLimits;
import antifraud.databaseentities.Transaction;
import antifraud.databaserepositories.CardLimitRepository;
import antifraud.databaserepositories.TransactionRepository;
import antifraud.requests.FeedbackRequest;
import antifraud.requests.TransactionRequest;
import antifraud.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Service
public class TransactionService {

    private final long ALLOWED_SUM = 200;
    private final long MANUAL_PROCESSING_SUM = 1500;
    private final SecurityService securityService;
    private final TransactionRepository transactionRepository;
    private final CardLimitRepository cardLimitRepository;

    @Autowired
    public TransactionService(SecurityService securityService, TransactionRepository transactionRepository, CardLimitRepository cardLimitRepository) {
        this.securityService = securityService;
        this.transactionRepository = transactionRepository;
        this.cardLimitRepository = cardLimitRepository;
    }

    public ResponseEntity<?> postTransaction(TransactionRequest transactionRequest) {
        if (transactionRequest.getAmount() != null && transactionRequest.getAmount() > 0
                && checkRegionValidity(transactionRequest.getRegion())
                && securityService.checkIpAddress(transactionRequest.getIp())) {
            Transaction transaction = createTransactionAndLimits(transactionRequest);
            TransactionInfo transactionInfo = buildReturnedInfo(transaction);
            transaction.setResult(transactionInfo.getReturnedInfo());
            transactionRepository.save(transaction);
            return new ResponseEntity<>(Map.of("result", transactionInfo.getReturnedInfo(),
                    "info", transactionInfo.getReasons()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private Transaction createTransactionAndLimits(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction(transactionRequest);
        if (cardLimitRepository.findByCardNumber(transaction.getNumber()) == null) {
            CardLimits cardLimits = new CardLimits(transaction.getNumber());
            cardLimits.setAllowedLimit(ALLOWED_SUM);
            cardLimits.setManualProcessingLimit(MANUAL_PROCESSING_SUM);
            cardLimitRepository.save(cardLimits);
        }
        return transaction;
    }

    private TransactionInfo buildReturnedInfo(Transaction transaction) {
        TransactionInfo transactionInfo = new TransactionInfo();
        ArrayList<String> reasonArray = new ArrayList<>();
        checkIPResult(transactionInfo, transaction, reasonArray);
        checkCardResult(transactionInfo, transaction, reasonArray);
        checkAmountResult(transactionInfo,transaction, reasonArray);
        checkCorrelationResult(transactionInfo, transaction, reasonArray);
        if (!reasonArray.isEmpty()) {
            reasonArray.sort(String::compareTo);
            transactionInfo.setReasons(String.join(", ", reasonArray));
        }
        return transactionInfo;

    }

    private void checkAmountResult(TransactionInfo transactionInfo, Transaction transaction, ArrayList<String> reasonArray) {
        String amountResult = transaction.getAmount() <= getCardLimit(transaction.getNumber()).getAllowedLimit() ? TransactionStatus.ALLOWED.name()
                : transaction.getAmount() > getCardLimit(transaction.getNumber()).getManualProcessingLimit() ? TransactionStatus.PROHIBITED.name()
                : TransactionStatus.MANUAL_PROCESSING.name();
        if (!(transactionInfo.getReturnedInfo().equals(TransactionStatus.PROHIBITED.name())
                && transaction.getAmount() <= getCardLimit(transaction.getNumber()).getManualProcessingLimit())) {
            transactionInfo.setReturnedInfo(amountResult);
            if (!transactionInfo.getReturnedInfo().equals(TransactionStatus.ALLOWED.name())) {
                reasonArray.add("amount");
            }
        }
    }

    private void checkCardResult(TransactionInfo transactionInfo, Transaction transaction, ArrayList<String> reasonArray) {
        if (securityService.checkIfStolen(transaction.getNumber())) {
            reasonArray.add("card-number");
            transactionInfo.setReturnedInfo(TransactionStatus.PROHIBITED.name());
        }
    }

    private void checkIPResult(TransactionInfo transactionInfo, Transaction transaction, ArrayList<String> reasonArray) {
        if (securityService.checkIfSuspicious(transaction.getIp())) {
            reasonArray.add("ip");
            transactionInfo.setReturnedInfo(TransactionStatus.PROHIBITED.name());
        }
    }

    private void checkCorrelationResult(TransactionInfo transactionInfo, Transaction transaction, ArrayList<String> reasonArray) {
        List<Transaction> transactionsSameCard = new ArrayList<>(transactionRepository.findAllByNumber(transaction.getNumber()));

        if (transactionsSameCard.size() > 0) {
            Predicate<Transaction> predicateRegions = x -> x.getRegion().equals(transaction.getRegion());
            Predicate<Transaction> predicateIPs = x -> x.getIp().equals(transaction.getIp());

            List<String> filteredRegionList = transactionsSameCard.stream()
                    .filter(predicateRegions.negate())
                    .filter(x -> ChronoUnit.MINUTES.between(x.getDate(), transaction.getDate()) <= 60
                            && ChronoUnit.MINUTES.between(x.getDate(), transaction.getDate()) > 0)
                    .map(Transaction::getRegion)
                    .distinct()
                    .toList();
            List<String> filteredIPsList = transactionsSameCard.stream()
                    .filter(predicateIPs.negate())
                    .filter(x -> ChronoUnit.MINUTES.between(x.getDate(), transaction.getDate()) <= 60
                            && ChronoUnit.MINUTES.between(x.getDate(), transaction.getDate()) > 0)
                    .map(Transaction::getIp)
                    .distinct()
                    .toList();
            if (filteredIPsList.size() > 1) {
                transactionInfo.setReturnedInfo(TransactionStatus.MANUAL_PROCESSING.name());
                if (filteredIPsList.size() > 2) {
                    transactionInfo.setReturnedInfo(TransactionStatus.PROHIBITED.name());
                }
                reasonArray.add("ip-correlation");
            }
            if (filteredRegionList.size() > 1) {
                transactionInfo.setReturnedInfo(TransactionStatus.MANUAL_PROCESSING.name());
                if (filteredRegionList.size() > 2) {
                    transactionInfo.setReturnedInfo(TransactionStatus.PROHIBITED.name());
                }
                reasonArray.add("region-correlation");
            }
        }
    }

    private boolean checkRegionValidity(String region) {
        for (TransactionRegionCode regionElement : TransactionRegionCode.values()) {
            if (regionElement.name().equals(region)) {
                return true;
            }
        }
        return false;
    }


    public ResponseEntity<?> putTransaction(FeedbackRequest feedbackRequest) {
        Transaction transaction = transactionRepository.findByTransactionId(feedbackRequest.getTransactionId());
        if (transaction != null && checkTransactionStatus(feedbackRequest.getFeedback())) {
            if (!feedbackRequest.getFeedback().equals(transaction.getResult())) {
                if (Objects.equals(transaction.getFeedback(), "")) {
                    changeLimitsFromFeedback(transaction, feedbackRequest.getFeedback());
                    transactionRepository.save(transaction);
                    return new ResponseEntity<>(transaction, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void changeLimitsFromFeedback(Transaction transaction, String feedback) {
        if (transaction.getResult().equals("ALLOWED") && feedback.equals("MANUAL_PROCESSING")) {
            changeAllowedLimit(transaction, false);
        } else
        if (transaction.getResult().equals("MANUAL_PROCESSING") && feedback.equals("ALLOWED")) {
            changeAllowedLimit(transaction, true);
        } else
        if (transaction.getResult().equals("ALLOWED") && feedback.equals("PROHIBITED")) {
            changeAllowedLimit(transaction, false);
            changeManualLimit(transaction, false);
        } else
        if (transaction.getResult().equals("PROHIBITED") && feedback.equals("ALLOWED")) {
            changeAllowedLimit(transaction, true);
            changeManualLimit(transaction, true);
        } else
        if (transaction.getResult().equals("MANUAL_PROCESSING") && feedback.equals("PROHIBITED")) {
            changeManualLimit(transaction, false);
        } else
        if (transaction.getResult().equals("PROHIBITED") && feedback.equals("MANUAL_PROCESSING")) {
            changeManualLimit(transaction, true);

        }
        transaction.setFeedback(feedback);
    }
    private void changeAllowedLimit(Transaction transaction, boolean shouldIncreaseAllowedLimit) {
        getCardLimit(transaction.getNumber()).setAllowedLimit(shouldIncreaseAllowedLimit ?
                Double.valueOf(Math.ceil(0.8 * getCardLimit(transaction.getNumber()).getAllowedLimit() + 0.2 * transaction.getAmount())).longValue() :
                Double.valueOf(Math.ceil(0.8 * getCardLimit(transaction.getNumber()).getAllowedLimit() - 0.2 * transaction.getAmount())).longValue());
    }

    private void changeManualLimit(Transaction transaction, boolean shouldIncreaseManualLimit) {
        getCardLimit(transaction.getNumber()).setManualProcessingLimit(shouldIncreaseManualLimit ?
                Double.valueOf(Math.ceil(0.8 * getCardLimit(transaction.getNumber()).getManualProcessingLimit() + 0.2 * transaction.getAmount())).longValue() :
                Double.valueOf(Math.ceil(0.8 * getCardLimit(transaction.getNumber()).getManualProcessingLimit() - 0.2 * transaction.getAmount())).longValue());
    }

    public ResponseEntity<?> getTransactionsHistory(String cardNumber) {
        if (securityService.checkCardLuhnValidity(cardNumber)) {
            List<Transaction> transactions = transactionRepository.findAllByNumber(cardNumber);
            if (transactions.size() > 0) {

                return new ResponseEntity<>(transactions, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getTransactionsHistory() {
        Iterable<Transaction> transactions = transactionRepository.findAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    private boolean checkTransactionStatus(String status) {
        for (TransactionStatus statusElement : TransactionStatus.values()) {
            if (statusElement.name().equals(status)) {
                return true;
            }
        }
        return false;
    }

    private CardLimits getCardLimit(String cardNumber) {
        return cardLimitRepository.findByCardNumber(cardNumber);
    }

}