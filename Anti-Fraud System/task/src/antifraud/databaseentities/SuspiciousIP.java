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
@Table(name = "suspicious-ips")
public class SuspiciousIP {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    @JsonProperty("id")
    private long ipId;
    @Column
    @JsonProperty("ip")
    private String ipNumber;

    public SuspiciousIP(String ipNumber) {
        this.ipNumber = ipNumber;
    }

}
