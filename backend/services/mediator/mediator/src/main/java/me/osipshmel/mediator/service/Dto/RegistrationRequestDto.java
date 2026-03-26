package me.osipshmel.mediator.service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import me.osipshmel.mediator.user.validator.UniqueUser.UniqueUser;

@UniqueUser
public record RegistrationRequestDto(
        @NotBlank(message = "Enter username")
        String username,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Enter email")
        String email,

        @Size(min = 8, message = "Min password size is 8 characters")
        @Size(max = 128, message = "Max password size is 128 characters")
        String password) {}
