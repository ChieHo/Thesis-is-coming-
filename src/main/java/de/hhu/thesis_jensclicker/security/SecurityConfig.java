package de.hhu.thesis_jensclicker.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication
public class SecurityConfig {
    @Bean
    public SecurityFilterChain configure(HttpSecurity chainBuilder) throws Exception {
        chainBuilder.authorizeHttpRequests(
                configurer -> configurer
                        .requestMatchers("/css/**", "/gh-login", "/about", "/public/**").permitAll()
                        .anyRequest().authenticated()
        ).oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/", true));

        return chainBuilder.build();
    }
}