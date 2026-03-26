package me.osipshmel.mediator.service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import me.osipshmel.mediator.user.validator.ExistsUser.ExistsUser;

/*@ExistsUser*/
public record LoginRequestDto(
        @NotBlank(message = "Enter username")
        String username,

        @NotBlank(message = "Enter password")
        String password
) {
}
