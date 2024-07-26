package antifraud.security;

import antifraud.databaseentities.StolenCard;
import antifraud.databaseentities.SuspiciousIP;
import antifraud.databaserepositories.CardRepository;
import antifraud.databaserepositories.SuspiciousIPsRepository;
import antifraud.requests.CardRequest;
import antifraud.requests.IPRequest;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class SecurityService {

    @Autowired
    private final SuspiciousIPsRepository suspiciousIPsRepository;
    @Autowired
    private final CardRepository cardRepository;

    public SecurityService(SuspiciousIPsRepository suspiciousIPsRepository, CardRepository cardRepository) {
        this.suspiciousIPsRepository = suspiciousIPsRepository;
        this.cardRepository = cardRepository;
    }

    public ResponseEntity<?> addSuspiciousIP(IPRequest ipRequest) {
        SuspiciousIP suspiciousIP = new SuspiciousIP(ipRequest.getIpNumber());
        if (!checkIpAddress(suspiciousIP.getIpNumber())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (suspiciousIPsRepository.findSuspiciousIPByIpNumber(suspiciousIP.getIpNumber()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        suspiciousIPsRepository.save(suspiciousIP);
        return new ResponseEntity<>(Map.of("id", suspiciousIP.getIpId(),
                "ip", suspiciousIP.getIpNumber()), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSuspiciousIP(String ipRequest) {
        if (checkIpAddress(ipRequest)) {
            SuspiciousIP suspiciousIP = suspiciousIPsRepository.findSuspiciousIPByIpNumber(ipRequest);
            if (suspiciousIP != null) {
                suspiciousIPsRepository.delete(suspiciousIP);
                return new ResponseEntity<>(Map.of("status",
                        "IP " + ipRequest + " successfully removed!" ), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<?> getSuspiciousIP() {
        Iterable<SuspiciousIP> suspiciousIPs = suspiciousIPsRepository.findAll();
        return new ResponseEntity<>(suspiciousIPs, HttpStatus.OK);
    }

    public ResponseEntity<?> addStolenCard(CardRequest cardRequest) {
        if (checkCardLuhnValidity(cardRequest.getNumber())) {
            StolenCard stolenCard = new StolenCard(cardRequest.getNumber());
            if(cardRepository.findStolenCardByCardNumber(cardRequest.getNumber()) == null) {
                cardRepository.save(stolenCard);
                return new ResponseEntity<>(stolenCard, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> deleteStolenCard(String cardRequest) {
        if (LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardRequest)) {
            StolenCard stolenCard = cardRepository.findStolenCardByCardNumber(cardRequest);
            if (stolenCard != null) {
                cardRepository.delete(stolenCard);
                return new ResponseEntity<>(Map.of("status",
                        "Card " + cardRequest + " successfully removed!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getStolenCard() {
        Iterable<StolenCard> stolenCards = cardRepository.findAll();
        return new ResponseEntity<>(stolenCards, HttpStatus.OK);
    }

    public boolean checkIfStolen(String card) {
        StolenCard stolenCard = cardRepository.findStolenCardByCardNumber(card);
        return !Objects.equals(null, stolenCard);
    }

    public boolean checkIfSuspicious(String ip) {
        SuspiciousIP suspiciousIP = suspiciousIPsRepository.findSuspiciousIPByIpNumber(ip);
        return !Objects.equals(null, suspiciousIP);
    }

    public boolean checkIpAddress(String ip) {
        int maxPossibleIpLength = 15;
        int minPossibleIpLength = 7;
        String[] ipParts = ip.split("\\.");
        if ((ip.length() <= maxPossibleIpLength && ip.length() >= minPossibleIpLength) &&
                ipParts.length == 4) {
            for (String part : ipParts) {
                if (Integer.parseInt(part) > 255 || Integer.parseInt(part) < 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean checkCardLuhnValidity(String cardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardNumber);
    }

}