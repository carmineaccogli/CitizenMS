package it.smartcitywastemanagement.citizenms.validators;

import it.smartcitywastemanagement.citizenms.domain.Address;
import it.smartcitywastemanagement.citizenms.mappers.CitizenMapper;
import it.smartcitywastemanagement.citizenms.service.AddressValidationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class ValidAddressImpl implements ConstraintValidator<ValidAddress, Address> {

    @Autowired
    AddressValidationService addressValidationService;
    private static final Logger log = LoggerFactory.getLogger(ValidAddressImpl.class);


    @Override
    public boolean isValid(Address value, ConstraintValidatorContext context) {


        if (value == null)
            return false;

        if (value.getCity() == null || value.getPostalCode() == null ||
                value.getStreetName() == null || value.getStreetNumber() == null)
            return false;

        Mono<Boolean> validationMono = addressValidationService.checkAddress(value);
        return validationMono.blockOptional().orElse(false);
    }
}
