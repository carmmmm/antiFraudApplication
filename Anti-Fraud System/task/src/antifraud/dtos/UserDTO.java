package antifraud.dtos;


import antifraud.enums.Role;

public class UserDTO {
    private String name;
    private String username;
    private Long id;
    private Role role;


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO(String name, String username, Long id, Role role) {
        this.name = name;
        this.username = username;
        this.id = id;
        this.role = role;
    }
}
