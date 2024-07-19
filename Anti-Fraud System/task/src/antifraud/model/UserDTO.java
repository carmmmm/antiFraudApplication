package antifraud.model;

public class UserDTO {
    private String name;
    private String username;
    private Long id;



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

    public UserDTO(String name, String username, Long id) {
        this.name = name;
        this.username = username;
        this.id = id;
    }
}
