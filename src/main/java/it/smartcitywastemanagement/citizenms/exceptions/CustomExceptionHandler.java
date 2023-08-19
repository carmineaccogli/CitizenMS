package it.smartcitywastemanagement.citizenms.exceptions;
import it.smartcitywastemanagement.citizenms.dto.ExceptionDTO;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  Quando una eccezione viene sollevata, questa classe si occupa di restituire nel body
 *  un json contenente le informazioni sull'eccezione.
 *  Informazioni eccezione:
 *      - codice eccezione (fa riferimento ad un elenco di eccezioni già stilato su un documento excel)
 *      - nome eccezione (nome della Classe Java relativa all'eccezione)
 *      - descrizione eccezione
 */
@ControllerAdvice
public class CustomExceptionHandler  {

    @ExceptionHandler(CitizenNotFoundException.class)
    public ResponseEntity<Object> handleSpecificException(CitizenNotFoundException ex) {
        // Creare un oggetto di risposta personalizzato per l'eccezione specifica
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(
                        15,
                        CitizenNotFoundException.class.getSimpleName(),
                        "Citizen not found"
                ));
    }

    @ExceptionHandler(FileNotValidException.class)
    public ResponseEntity<Object> handleSpecificException(FileNotValidException ex) {
        // Creare un oggetto di risposta personalizzato per l'eccezione specifica
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        16,
                        FileNotValidException.class.getSimpleName(),
                        "File is empty or not in valid format CSV"
                ));
    }

    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<Object> handleSpecificException(org.springframework.dao.DuplicateKeyException ex) {
        // Creare un oggetto di risposta personalizzato per l'eccezione specifica
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionDTO(
                        17,
                        DuplicateKeyException.class.getSimpleName(),
                        "A citizen with the same SSN already exists"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        StringBuilder errorString = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errorString.append(errorMessage).append(".");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        14,
                        MethodArgumentNotValidException.class.getSimpleName(),
                        errorString.deleteCharAt(errorString.length() - 1).toString()
                ));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleSpecificException(WebClientResponseException ex) {
        // Creare un oggetto di risposta personalizzato per l'eccezione specifica
        HttpStatusCode httpStatus = ex.getStatusCode();
        String responseBody = ex.getResponseBodyAsString();

        return ResponseEntity.status(httpStatus)
                .body(new ExceptionDTO(
                        18,
                        WebClientResponseException.class.getSimpleName(),
                        responseBody
                ));
    }














}

