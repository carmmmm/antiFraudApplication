package antifraud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;


//Configures Spring Security.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationEntryPoint restAuthenticationEntryPoint;


    public SecurityConfig(AuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/list").permitAll()
                .requestMatchers("/actuator/shutdown").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .build();
    }
}