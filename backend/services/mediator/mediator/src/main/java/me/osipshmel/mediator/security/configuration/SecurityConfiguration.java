package me.osipshmel.mediator.security.configuration;

import me.osipshmel.mediator.security.JwtFilter;
import me.osipshmel.mediator.security.handler.CustomAccessDeniedHandler;
import me.osipshmel.mediator.security.handler.LogoutHandlerImpl;
import me.osipshmel.mediator.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/public/**").permitAll()
                        // TODO!
                        // вероятно, через порты нужно будет организовать.
                        // Хотя internal вполне реально ключами подписывать
                        .requestMatchers("/internal/**").denyAll()
                        .requestMatchers("/system/**").denyAll()
                        .anyRequest().denyAll()
                )
                .userDetailsService(userService)
                .exceptionHandling(e -> {
                    e.accessDeniedHandler(accessDeniedHandler);
                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(log ->{
                    log.logoutUrl("/api/v1/auth/logout");
                    log.addLogoutHandler(logoutHandler);
                    log.logoutSuccessHandler((request,
                                              response,
                                              authentication) ->
                            SecurityContextHolder.clearContext());
                        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }
}
