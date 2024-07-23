package antifraud.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusResponse {
    private String status;

    public StatusResponse(String status) {
        this.status = status;
    }

}
