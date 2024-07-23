package antifraud.dtos;


import antifraud.enums.Role;


public class UserRegistrationDto {
    private String name;
    private String username;
    private String password;
    private Role role;
    private boolean isAccountLocked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public Role setRole(Role role) {
        this.role = role;
        return this.role;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked;
    }
}
