package antifraud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RoleUser implements GrantedAuthority {
    @Id
    private String id;

    @ManyToMany
    private final List<Operation> allowedOperations = new ArrayList<>();

    @Override
    public String getAuthority() {
        return id;
    }

    public List<Operation> getAllowedOperations() {
        return allowedOperations;
    }
}
