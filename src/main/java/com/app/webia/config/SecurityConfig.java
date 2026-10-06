package com.app.webia.config;

import com.app.webia.repository.UserRepository;
import com.app.webia.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return login -> userRepository.findByEmailIgnoreCase(login)
                .or(() -> userRepository.findByUsernameIgnoreCase(login))
                .filter(user -> user.getDeletedAt() == null)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/catalogo/**", "/producto/**", "/auth/**",
                                "/css/**", "/js/**", "/img/**", "/webjars/**", "/error",
                                "/api/ia/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/perfil/**", "/api/media/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }
}
