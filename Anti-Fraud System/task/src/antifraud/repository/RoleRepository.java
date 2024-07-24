package antifraud.repository;

import antifraud.model.RoleUser;
import antifraud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleUser, Long> {
    Optional<RoleUser> findByName(String username);
    List<RoleUser> findAll();
}
