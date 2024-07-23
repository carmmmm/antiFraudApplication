package antifraud.config;

import antifraud.service.CustomUserDetailsService;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import javax.management.relation.Role;


//Configures Spring Security.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {


    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public SecurityConfig(AuthenticationEntryPoint restAuthenticationEntryPoint) {
//        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
//    }
//
//    void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
//    }
//
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
//    }



    //    @Override
//    protected void configure(HttpSecurity http) throws Exception {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/auth/user").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole("ADMINISTRATOR", "MERCHANT", "SUPPORT")
                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .httpBasic()
                .and()
                .build();
    }
}

//    private final AuthenticationEntryPoint restAuthenticationEntryPoint;
//
//
//    public SecurityConfig(AuthenticationEntryPoint restAuthenticationEntryPoint) {
//        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .httpBasic()
//                .and()
//                .csrf().disable()
//                .exceptionHandling()
//                .authenticationEntryPoint(restAuthenticationEntryPoint)
//                .and()
//                .headers().frameOptions().disable()
//                .and()
//                .authorizeRequests()
//                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
//                .requestMatchers(HttpMethod.GET, "/api/auth/list").authenticated()
//                .requestMatchers("/actuator/shutdown").permitAll()
//                .requestMatchers("/h2-console/**").permitAll()
//                .requestMatchers(PathRequest.toH2Console()).permitAll()
//                .requestMatchers("/error").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .and()
//                .build();
//    }
//
//    @Bean
////    @Override
//    protected UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
