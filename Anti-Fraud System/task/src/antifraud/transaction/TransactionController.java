package antifraud.transaction;

import antifraud.requests.FeedbackRequest;
import antifraud.requests.TransactionRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antifraud/")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> postTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {
        return transactionService.postTransaction(transactionRequest);
    }

    @PutMapping("/transaction")
    public ResponseEntity<?> putTransaction(@RequestBody @Valid FeedbackRequest feedbackRequest) {
        return transactionService.putTransaction(feedbackRequest);
    }

    @GetMapping("/history/{cardNumber}")
    public ResponseEntity<?> getTransactionsHistory(@PathVariable String cardNumber) {
        return transactionService.getTransactionsHistory(cardNumber);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTransactionsHistory() {
        return transactionService.getTransactionsHistory();
    }
}