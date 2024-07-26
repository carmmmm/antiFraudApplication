package antifraud.databaseentities;

import antifraud.requests.TransactionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long transactionId;
    @Column
    private Long amount;
    @Column
    private String ip;
    @Column
    private String number;
    @Column
    private String region;
    @Column
    private LocalDateTime date;
    @Column
    private String result;
    @Column
    private String feedback;

    public Transaction(TransactionRequest transactionRequest) {
        this.amount = transactionRequest.getAmount();
        this.ip = transactionRequest.getIp();
        this.number = transactionRequest.getNumber();
        this.region = transactionRequest.getRegion();
        this.date = transactionRequest.getDate();
        this.result = "";
        this.feedback = "";
    }
}
