package antifraud.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserStatusDto {
    private String username;
    private String operation;

    public UserStatusDto(String username, String operation) {
        this.username = username;
        this.operation = operation;
    }

}
