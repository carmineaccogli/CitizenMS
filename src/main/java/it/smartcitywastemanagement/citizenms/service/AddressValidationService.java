package it.smartcitywastemanagement.citizenms.service;

import it.smartcitywastemanagement.citizenms.domain.Address;
import reactor.core.publisher.Mono;

public interface AddressValidationService {

    Mono<Boolean> checkAddress(Address addressToCheck);
}
