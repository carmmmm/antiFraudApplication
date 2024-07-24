package antifraud.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<RoleUser> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<RoleUser> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleUser> roles) {
        this.roles = roles;
    }
}
