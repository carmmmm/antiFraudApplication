package antifraud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import antifraud.model.Transaction;

//Handles transaction validation.
@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {

    @PostMapping("/transaction")
    public ResponseEntity<?> validateTransaction(@RequestBody Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction amount");
        }

        String result;
        long amount = transaction.getAmount();

        if (amount <= 200) {
            result = "ALLOWED";
        } else if (amount <= 1500) {
            result = "MANUAL_PROCESSING";
        } else {
            result = "PROHIBITED";
        }

        return ResponseEntity.ok().body("{\"result\": \"" + result + "\"}");
    }
}