package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/authenticate").authenticated()
                .anyRequest().permitAll())
                .formLogin(form -> form.permitAll().defaultSuccessUrl("/authenticate-results", true))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
   }

   @Bean
   public UserDetailsService userDetailsService() {
       UserDetails user = User.builder()
               .username("test")
               .password(passwordEncoder().encode("test"))
               .build();
       return new InMemoryUserDetailsManager(user);
   }
}
