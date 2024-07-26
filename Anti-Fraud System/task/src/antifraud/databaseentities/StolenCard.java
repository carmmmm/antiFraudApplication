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
@Table(name = "stolen-cards")
public class StolenCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @JsonProperty("id")
    private long cardId;
    @Column
    @JsonProperty("number")
    private String cardNumber;

    public StolenCard(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
