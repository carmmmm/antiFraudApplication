package antifraud.service;


import antifraud.exceptions.BadRequestException;
import antifraud.exceptions.ConflictException;
import antifraud.enums.UserStatus;
import antifraud.model.Privilege;
import antifraud.model.Role;
import antifraud.enums.RoleEnum;
import antifraud.model.User;
import antifraud.repository.RoleRepository;
import antifraud.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@Transactional
public class UserService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ;
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                !user.isTokenExpired(),
                getAuthorities(user.getRoles()));
    }


    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Role> roles) { return getGrantedAuthorities(getPrivileges(roles)); }

        private List<String> getPrivileges(Collection<Role> roles) {

           List<String> privileges = new ArrayList<>();
            List<Privilege> collection = new ArrayList<>();
            for (Role role : roles) {
                privileges.add(role.getName());
                collection.addAll(role.getPrivileges());
            }
            for (Privilege item : collection) {
                privileges.add(item.getName()) ;
            }
            return privileges;
        }

        private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String privilege : privileges) {
                authorities.add(new SimpleGrantedAuthority(privilege));
            }
            return authorities;
        }

//
//    public User registerUser(String name, String username, String password) {
//        Role role = userRepository.count() == 0 ? Role.ADMINISTRATOR : Role.MERCHANT;
//        UserStatus status = role == Role.ADMINISTRATOR ? UserStatus.ACTIVE : UserStatus.LOCKED;
//        User user = new User(name, username, password, role, status);
//        return userRepository.save(user);
//    }

    public User changeUserRole(String username, RoleEnum newRole) throws ConflictException {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getRole() == newRole) {
            throw new ConflictException("User already has the role " + newRole);
        }
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public User changeUserStatus(String username, UserStatus newStatus) throws BadRequestException {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getRole() == RoleEnum.ADMINISTRATOR) {
            throw new BadRequestException("Cannot change status of ADMINISTRATOR");
        }
        user.setStatus(newStatus);
        return userRepository.save(user);
    }
}
