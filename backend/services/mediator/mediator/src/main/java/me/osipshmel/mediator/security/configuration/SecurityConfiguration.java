package me.osipshmel.mediator.security.configuration;

import me.osipshmel.mediator.security.JwtFilter;
import me.osipshmel.mediator.security.handler.CustomAccessDeniedHandler;
import me.osipshmel.mediator.security.handler.LogoutHandlerImpl;
import me.osipshmel.mediator.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final UserService userService;
    private final LogoutHandlerImpl logoutHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfiguration(
            JwtFilter jwtFilter,
            UserService userService,
            LogoutHandlerImpl logoutHandler,
            CustomAccessDeniedHandler accessDeniedHandler
            ){
        this.jwtFilter = jwtFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.logoutHandler = logoutHandler;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception{

        http
                // межсайтовые подделки
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
