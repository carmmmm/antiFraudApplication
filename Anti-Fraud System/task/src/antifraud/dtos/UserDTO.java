package antifraud.dtos;



import antifraud.enums.RoleEnum;

public class UserDTO {
    private String name;
    private String username;
    private Long id;
    private RoleEnum role;


    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
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

    public UserDTO(String name, String username, Long id, RoleEnum role) {
        this.name = name;
        this.username = username;
        this.id = id;
        this.role = role;
    }
}
