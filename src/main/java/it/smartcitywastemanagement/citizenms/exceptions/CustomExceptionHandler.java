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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;
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
    public ResponseEntity<Object> citizenNotFoundHandler(CitizenNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(
                        7,
                        CitizenNotFoundException.class.getSimpleName(),
                        "Citizen not found"
                ));
    }

    @ExceptionHandler(FileNotValidException.class)
    public ResponseEntity<Object> fileNotValidHandler(FileNotValidException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        21,
                        FileNotValidException.class.getSimpleName(),
                        "File is empty or not in valid format CSV"
                ));
    }

    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<Object> duplicateKeyHandler(org.springframework.dao.DuplicateKeyException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionDTO(
                        22,
                        DuplicateKeyException.class.getSimpleName(),
                        "A citizen with the same SSN already exists"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidHandler (
            MethodArgumentNotValidException ex) {

        StringBuilder errorString = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errorString.append(errorMessage).append(".");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDTO(
                        17,
                        MethodArgumentNotValidException.class.getSimpleName(),
                        errorString.deleteCharAt(errorString.length() - 1).toString()
                ));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> webClientResponseHandler(WebClientResponseException ex) {

        HttpStatusCode httpStatus = ex.getStatusCode();
        String responseBody = ex.getResponseBodyAsString();

        return ResponseEntity.status(httpStatus)
                .body(new ExceptionDTO(
                        23,
                        WebClientResponseException.class.getSimpleName(),
                        responseBody
                ));
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Object> webClientRequestHandler(WebClientRequestException ex) {

        String responseBody = ex.getMessage();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDTO(
                        24,
                        WebClientRequestException.class.getSimpleName(),
                        responseBody
                ));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> noHandlerFoundHandler(NoHandlerFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getBody());
    }















}

