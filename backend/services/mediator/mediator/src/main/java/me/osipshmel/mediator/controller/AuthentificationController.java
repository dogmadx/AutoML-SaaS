package me.osipshmel.mediator.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.osipshmel.mediator.service.AuthenticationService;
import me.osipshmel.mediator.service.Dto.AuthenticationResponseDto;
import me.osipshmel.mediator.service.Dto.LoginRequestDto;
import me.osipshmel.mediator.service.Dto.RegistrationRequestDto;
import me.osipshmel.mediator.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/public/auth")
@RequiredArgsConstructor
@Validated
public class AuthentificationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegistrationRequestDto registrationDto
            ){
        authenticationService.register(registrationDto);

        return ResponseEntity.ok("Successful registration");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody LoginRequestDto request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        return authenticationService.refreshToken(request, response);
    }
}
