package antifraud.service;


import antifraud.exceptions.BadRequestException;
import antifraud.exceptions.ConflictException;
import antifraud.enums.Role;
import antifraud.enums.UserStatus;
import antifraud.model.User;
import antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(user.getStatus() == UserStatus.LOCKED)
                .build();
    }

    public User registerUser(String name, String username, String password) {
        Role role = userRepository.count() == 0 ? Role.ADMINISTRATOR : Role.MERCHANT;
        UserStatus status = role == Role.ADMINISTRATOR ? UserStatus.ACTIVE : UserStatus.LOCKED;
        User user = new User(name, username, password, role, status);
        return userRepository.save(user);
    }

    public User changeUserRole(String username, Role newRole) throws ConflictException {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getRole() == newRole) {
            throw new ConflictException("User already has the role " + newRole);
        }
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public User changeUserStatus(String username, UserStatus newStatus) throws BadRequestException {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getRole() == Role.ADMINISTRATOR) {
            throw new BadRequestException("Cannot change status of ADMINISTRATOR");
        }
        user.setStatus(newStatus);
        return userRepository.save(user);
    }

}