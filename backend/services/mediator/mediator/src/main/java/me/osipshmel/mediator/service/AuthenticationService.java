package me.osipshmel.mediator.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.osipshmel.mediator.user.exception.UserAlreadyExistsException;
import me.osipshmel.mediator.repository.TokenRepository;
import me.osipshmel.mediator.repository.UserRepository;
import me.osipshmel.mediator.repository.entity.Token;
import me.osipshmel.mediator.repository.entity.User;
import me.osipshmel.mediator.security.JwtService;
import me.osipshmel.mediator.user.UserRole;
import me.osipshmel.mediator.service.Dto.AuthenticationResponseDto;
import me.osipshmel.mediator.service.Dto.LoginRequestDto;
import me.osipshmel.mediator.service.Dto.RegistrationRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;

/*    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }*/

    @Transactional
    public void register(RegistrationRequestDto request){
/*
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email '" + request.email() + "' is already registered");
        }*/

        User user = new User();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        user = userRepository.save(user);
    }

    private void revokeAllToken(User user){
        List<Token> validTokens = tokenRepository.findAllAccessTokenByUser(user.getId());

        if(!validTokens.isEmpty()){
            validTokens.forEach(t ->{
                t.setLoggedOut(true);
                    });
        }

        // TODO!
        //  Think about it.
        //tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }

    @Transactional
    public AuthenticationResponseDto authenticate(LoginRequestDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // TODO!
        //  на сколько нормально, что этот еблан использует findByUsername а к-й нибудь валидатор?
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with username: " + request.username()));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllToken(user);
        saveUserToken(accessToken, refreshToken, user);

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );

        if(jwtService.isValidRefresh(token, user)){

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);
            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
