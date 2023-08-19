package it.smartcitywastemanagement.citizenms.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidAddressImpl.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAddress {

    String message() default "Invalid Address format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
