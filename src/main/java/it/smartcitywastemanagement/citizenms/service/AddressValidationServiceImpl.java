package it.smartcitywastemanagement.citizenms.service;


import it.smartcitywastemanagement.citizenms.domain.Address;
import it.smartcitywastemanagement.citizenms.mappers.CitizenMapper;
import it.smartcitywastemanagement.citizenms.validators.ValidAddressImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import reactor.core.publisher.Mono;

@Service
public class AddressValidationServiceImpl implements AddressValidationService{

    @Autowired
    private WebClient addressValidatorWebClient;


    public Mono<Boolean> checkAddress(Address addressToCheck) {

        /*String query = addressToCheck.getStreetNumber() + " " + addressToCheck.getStreetName() + ","
                        + addressToCheck.getCity() + ","
                        + addressToCheck.getPostalCode();*/


        return addressValidatorWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        //.queryParam("q",query)
                        .queryParam("city",addressToCheck.getCity())
                        .queryParam("postalcode",addressToCheck.getPostalCode())
                        .queryParam("street",addressToCheck.getStreetName())
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToFlux(Object.class)
                .collectList()
                .map(responseBody -> !responseBody.isEmpty());
                //.map(responseBody -> responseBody.isEmpty());
    }


}
