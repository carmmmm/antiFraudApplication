package antifraud.controller;

import antifraud.dtos.UserDTO;
import antifraud.dtos.UserRoleDto;
import antifraud.dtos.UserStatusDto;
import antifraud.enums.Role;
import antifraud.enums.UserStatus;
import antifraud.model.*;
import antifraud.repository.UserRepository;
import antifraud.exceptions.BadRequestException;
import antifraud.exceptions.ConflictException;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

//Handles user registration, listing, and deletion.
@EnableMethodSecurity
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;




    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null|| user.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        // Determine role based on existing users
        Role role = userRepository.count() == 0 ? Role.ADMINISTRATOR : Role.MERCHANT;
        user.setRole(role);

        // Set default locked status
        user.setAccountLocked(role == Role.ADMINISTRATOR ? false : true);

        // Encode the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Prepare the response
        UserDTO userDTO = new UserDTO(savedUser.getName(), savedUser.getUsername(), savedUser.getId(), savedUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PutMapping("/role")
    public ResponseEntity<User> changeUserRole(@RequestBody UserRoleDto userRoleDto) throws ConflictException {
        User user = userService.changeUserRole(userRoleDto.getUsername(), userRoleDto.getRole());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/access")
    public ResponseEntity<StatusResponse> changeUserStatus(@RequestBody UserStatusDto userStatusDto) throws BadRequestException {
        User user = userService.changeUserStatus(userStatusDto.getUsername(), UserStatus.valueOf(userStatusDto.getOperation()));
        return new ResponseEntity<>(new StatusResponse("User " + user.getUsername() + " " + user.getStatus().name().toLowerCase() + "!"), HttpStatus.OK);
    }


    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MERCHANT', 'SUPPORT')")
    public ResponseEntity<?> listUsers(@RequestBody User user) throws ConflictException {
        if (user.getRole() != Role.ADMINISTRATOR || user.getRole() != Role.MERCHANT || user.getRole() != Role.SUPPORT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<User> userss = userRepository.findAll();
        // Transform users to DTOs if needed
        List<UserDTO> userDTOs = userss.stream()
                .map(users -> new UserDTO(users.getName(), users.getUsername(), users.getId(), users.getRole()))
                .collect(Collectors.toList());
        userss.forEach(users -> System.out.println("Id: " + users.getId() + "User: " + users.getName() + " - " + users.getUsername() + "Role: " + users.getRole()));
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
