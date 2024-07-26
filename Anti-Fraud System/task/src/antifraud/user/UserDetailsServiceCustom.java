package antifraud.user;

import antifraud.databaseentities.User;
import antifraud.databaserepositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceCustom(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsernameIgnoreCase(username);
        if (user != null) {
            antifraud.user.UserDetails userDetails = new antifraud.user.UserDetails(user);
            return userDetails;
        }
        throw new UsernameNotFoundException("User not found");
    }
}