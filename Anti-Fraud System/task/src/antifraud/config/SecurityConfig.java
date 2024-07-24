package antifraud.config;


import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;



//Configures Spring Security.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {

    @Autowired
    private UserService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                //Authorize requests section:
                .authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/api/auth/user/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole("ADMINISTRATOR")
               // .requestMatchers(HttpMethod.GET, "/api/auth/list/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasRole("MERCHANT")
                .requestMatchers(HttpMethod.PUT, "/api/auth/access/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/auth/role/**").hasRole("ADMINISTRATOR")

                //h2 console section
                .requestMatchers("/actuator/shutdown").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .anyRequest().authenticated()
                .and().headers().frameOptions().sameOrigin()
                .and()
                .sessionManagement().invalidSessionUrl("/session-invalid") // Redirect URL for invalid sessions
                .maximumSessions(1) // Maximum sessions per user
                .expiredUrl("/session-expired") // Redirect URL for expired sessions
                .and().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .formLogin().disable()
                .httpBasic()
                .and()
                .build();
    }
}
