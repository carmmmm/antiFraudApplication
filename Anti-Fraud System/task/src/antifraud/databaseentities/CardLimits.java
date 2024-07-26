package antifraud.databaseentities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


@Validated
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "credit-cards-limits")
public class CardLimits {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @JsonProperty("id")
    private long cardId;
    @Column
    private String cardNumber;
    @Column
    private long allowedLimit = 0;
    @Column
    private long manualProcessingLimit = 0;

    public CardLimits(String cardNumber, long allowedLimit, long manualProcessingLimit) {
        this.cardNumber = cardNumber;
        this.allowedLimit = allowedLimit;
        this.manualProcessingLimit = manualProcessingLimit;
    }

    public CardLimits(String cardNumber) {
        this.cardNumber = cardNumber;
        this.allowedLimit = 0;
        this.manualProcessingLimit = 0;
    }
}