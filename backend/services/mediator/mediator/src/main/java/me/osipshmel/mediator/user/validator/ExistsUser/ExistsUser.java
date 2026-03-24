package me.osipshmel.mediator.user.validator.ExistsUser;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsUserValidator.class)
public @interface ExistsUser {
    String message() default "User not found";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
