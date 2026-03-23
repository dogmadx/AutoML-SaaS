package me.osipshmel.mediator.controller;

import lombok.RequiredArgsConstructor;
import me.osipshmel.mediator.service.AuthenticationService;
import me.osipshmel.mediator.service.Dto.RegistrationRequestDto;
import me.osipshmel.mediator.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthentificationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(
            @RequestBody RegistrationRequestDto registrationDto
            ){
        //TODO!
        // попа
        return null;
    }
}
