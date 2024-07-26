package antifraud.authentication;

import antifraud.authorizationmodels.Role;
import antifraud.databaseentities.User;
import antifraud.databaserepositories.UserRepository;
import antifraud.requests.AccountLockRequest;
import antifraud.requests.ChangeRoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
public class AuthenticationService {

    @Autowired
    private PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> registerUser(User user) {
        if (userRepository.findUserByUsernameIgnoreCase(user.getUsername()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        giveRolesAndUnlock(user);
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteUser(String username) {
        User user = userRepository.findUserByUsernameIgnoreCase(username);
        if (user != null) {
            userRepository.deleteById(user.getUserId());
            return new ResponseEntity<>(Map.of("username", username,
                    "status", "Deleted successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> userList() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    public ResponseEntity<?> changeRoles(ChangeRoleRequest changeRoleRequest) {
        User user = userRepository.findUserByUsernameIgnoreCase(changeRoleRequest.getUsername());
        Role roleRequest = findRole(changeRoleRequest.getRole());
        if (user != null) {
            if (Objects.equals(roleRequest, Role.MERCHANT) ||
                    Objects.equals(roleRequest, Role.SUPPORT)) {
                if (!user.getRole().name().equals(roleRequest.name())) {
                    user.setRole(findRole(changeRoleRequest.getRole()));
                    userRepository.save(user);
                    return new ResponseEntity<>(user, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> changeAccountLock(AccountLockRequest accountLockRequest) {
        User user = userRepository.findUserByUsernameIgnoreCase(accountLockRequest.getUsername());
        ResponseEntity<Map<String, String>> response;
        if (Objects.equals(accountLockRequest.getOperation(), "UNLOCK")) {
            user.setAccountNonLocked(true);
            response = new ResponseEntity<>(Map.of("status",
                    "User " + accountLockRequest.getUsername() + " unlocked!"), HttpStatus.OK);
            userRepository.save(user);
            return response;
        } else if (Objects.equals(accountLockRequest.getOperation(), "LOCK") &&
                !Role.ADMINISTRATOR.name().equals(user.getRole().name())) {
            user.setAccountNonLocked(false);
            response = new ResponseEntity<>(Map.of("status",
                    "User " + accountLockRequest.getUsername() + " locked!"), HttpStatus.OK);
            userRepository.save(user);
            return response;
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void giveRolesAndUnlock(User user) {
        if (StreamSupport.stream(userRepository.findAll().spliterator(), false).findAny().isEmpty()) {
            user.setRole(Role.ADMINISTRATOR);
            user.setAccountNonLocked(true);
        } else {
            user.setRole(Role.MERCHANT);
            user.setAccountNonLocked(false);
        }
        userRepository.save(user);
    }

    private Role findRole(String roleName) {
        for (Role r : Role.values()) {
            if (Objects.equals(r.name(), roleName)) {
                return r;
            }
        }
        return null;
    }


}