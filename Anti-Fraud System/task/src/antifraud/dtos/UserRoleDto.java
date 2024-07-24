package antifraud.dtos;

import antifraud.enums.RoleEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRoleDto {
    private String username;
    private RoleEnum role;
}
