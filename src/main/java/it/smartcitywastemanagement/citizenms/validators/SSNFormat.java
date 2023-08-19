package it.smartcitywastemanagement.citizenms.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Pattern(regexp = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$", message = "Invalid Italian fiscal code format")
@Constraint(validatedBy = {})
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SSNFormat {
    String message() default "Invalid Italian fiscal code format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
