package me.osipshmel.mediator.user.validator.ExistsUser;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import me.osipshmel.mediator.repository.UserRepository;
import me.osipshmel.mediator.service.Dto.LoginRequestDto;
import me.osipshmel.mediator.service.Dto.RegistrationRequestDto;
import me.osipshmel.mediator.user.validator.UniqueUser.UniqueUser;

@AllArgsConstructor
public class ExistsUserValidator implements ConstraintValidator<ExistsUser, LoginRequestDto> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(LoginRequestDto dto, ConstraintValidatorContext context) {
        boolean usernameExists = userRepository.existsByUsername(dto.username());

        if(!usernameExists){
            context.disableDefaultConstraintViolation();
            return false;
        }

        return true;
    }
}

