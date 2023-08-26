package it.smartcitywastemanagement.citizenms.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    private final String API_VALIDATING_ADDRESS = "https://nominatim.openstreetmap.org";

    private final String API_LOGIN_MS = "http://login_service:8080/api/authentication";

    @Bean
    public WebClient addressValidatorWebClient(WebClient.Builder webClientBuilder) {

        ExchangeFilterFunction errorHandlingFilter = ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return Mono.just(clientResponse);
                }
        );

        return webClientBuilder
                .baseUrl(API_VALIDATING_ADDRESS)
                .filter(errorHandlingFilter)
                .build();
    }


    @Bean
    public WebClient createUserWebClient(WebClient.Builder webClientBuilder) {

        ExchangeFilterFunction errorHandlingFilter = ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return Mono.just(clientResponse);
                });

        return webClientBuilder
                .baseUrl(API_LOGIN_MS)
                .filter(errorHandlingFilter)
                .build();
    }
}
