package antifraud.authentication;

import antifraud.databaseentities.User;
import antifraud.requests.AccountLockRequest;
import antifraud.requests.ChangeRoleRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        return authenticationService.registerUser(user);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return authenticationService.deleteUser(username);
    }

    @GetMapping("/list")
    public ResponseEntity<?> userList() {
        return authenticationService.userList();
    }

    @PutMapping("/role")
    public ResponseEntity<?> changeRoles(@RequestBody ChangeRoleRequest changeRoleRequest) {
        return authenticationService.changeRoles(changeRoleRequest);
    }

    @PutMapping("/access")
    public ResponseEntity<?> changeAccountLock(@RequestBody AccountLockRequest accountLockRequest) {
        return authenticationService.changeAccountLock(accountLockRequest);
    }
}