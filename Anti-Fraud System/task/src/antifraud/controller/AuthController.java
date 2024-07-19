package antifraud.controller;

import antifraud.model.User;
import antifraud.model.UserDTO;
import antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

//Handles user registration, listing, and deletion.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null|| user.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO userDTO = new UserDTO(savedUser.getName(), savedUser.getUsername(), savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listUsers() {
        List<User> users = userRepository.findAll();
        // Transform users to DTOs if needed
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getName(), user.getUsername(), user.getId()))
                .collect(Collectors.toList());
        users.forEach(user -> System.out.println("User: " + user.getName() + " - " + user.getUsername()));
        return ResponseEntity.ok(userDTOs);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
        return ResponseEntity.ok().body("{\"username\": \"" + username + "\", \"status\": \"Deleted successfully!\"}");
    }
}
