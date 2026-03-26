package me.osipshmel.mediator.user.validator.UniqueUser;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import me.osipshmel.mediator.repository.UserRepository;
import me.osipshmel.mediator.service.Dto.RegistrationRequestDto;

@RequiredArgsConstructor
public class UniqueUserValidator implements ConstraintValidator<UniqueUser, RegistrationRequestDto> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(RegistrationRequestDto dto, ConstraintValidatorContext context) {
        boolean usernameExists = userRepository.existsByUsername(dto.username());
        boolean emailExists = userRepository.existsByEmail(dto.email());

        if(usernameExists || emailExists){
            context.disableDefaultConstraintViolation();
            if(usernameExists){
                context.buildConstraintViolationWithTemplate("Username is taken")
                        .addPropertyNode("username").addConstraintViolation();
            }
            if(emailExists){
                context.buildConstraintViolationWithTemplate("Email is taken")
                        .addPropertyNode("email").addConstraintViolation();
            }
            return false;
        }

        return true;
    }
}
