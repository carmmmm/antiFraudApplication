package antifraud.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInfo {
    private String returnedInfo = "ALLOWED";
    private String reasons = "none";
}
