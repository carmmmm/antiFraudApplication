package antifraud.dtos;

import antifraud.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRoleDto {
    private String username;
    private Role role;
}
