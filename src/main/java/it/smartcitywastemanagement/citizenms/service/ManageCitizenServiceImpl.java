package it.smartcitywastemanagement.citizenms.service;


import ch.qos.logback.core.net.server.Client;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import it.smartcitywastemanagement.citizenms.configuration.WebClientConfiguration;
import it.smartcitywastemanagement.citizenms.domain.Address;
import it.smartcitywastemanagement.citizenms.domain.Citizen;
import it.smartcitywastemanagement.citizenms.dto.CitizenDTO;
import it.smartcitywastemanagement.citizenms.dto.CitizenRegistrationDTO;
import it.smartcitywastemanagement.citizenms.exceptions.CitizenNotFoundException;
import it.smartcitywastemanagement.citizenms.mappers.CitizenMapper;
import it.smartcitywastemanagement.citizenms.repositories.CitizenRepository;
import it.smartcitywastemanagement.citizenms.security.JwtUtilities;
import it.smartcitywastemanagement.citizenms.utility.CSVProcessingUtility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ManageCitizenServiceImpl implements ManageCitizenService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private CSVProcessingUtility csvProcessingUtility;

    @Autowired
    private CitizenMapper citizenMapper;

    @Autowired
    private WebClient createUserWebClient;

    @Autowired
    private JwtUtilities jwtUtilities;

    private static final Logger logger = LoggerFactory.getLogger(ManageCitizenServiceImpl.class);
    



    public String addNewCitizen(Citizen citizen) throws DuplicateKeyException, WebClientResponseException {

        // salvataggio citizen
        Citizen newCitizen = citizenRepository.save(citizen);

        // chiamata all'API di LOGINMS per creare lo user per l'accesso al sistema
        String userID_created = APICALL_createUser(newCitizen);

        // chiamata all'API successfull
        // inserimento dello user ID appena creato
        newCitizen.setUserId(userID_created);

        // update del cittadino
        citizenRepository.save(newCitizen);


        return newCitizen.getId();
    }


    public List<Citizen> findAllCitizens() {
        return citizenRepository.findAll();
    }

    public String findCitizenIdByUserId(String userID) throws CitizenNotFoundException {

        Optional<Citizen> optCitizen = citizenRepository.findByUserId(userID);
        if(!optCitizen.isPresent())
            throw new CitizenNotFoundException();

        return optCitizen.get().getId();
    }

    public Citizen findCitizenById(String citizenID) throws CitizenNotFoundException {

        Citizen citizen = null;

        // Controllo l'esistenza della richiesta di allocazione
        Optional<Citizen> optCitizen = citizenRepository.findCitizenById(citizenID);
        if(!optCitizen.isPresent())
            throw new CitizenNotFoundException();

        citizen = optCitizen.get();

        return citizen;
    }


    public List<String> loadCitizensFromFile(MultipartFile file) throws CsvException, IOException {

        // Array di utilità
        List<String> citizenIDs = new ArrayList<>();    // result
        List<Citizen> batchCitizen = new ArrayList<>(); // citizen da aggiungere al db

        // Lettura del file
        List<String[]> rows = csvProcessingUtility.readCSVFile(file);

        // Ottenimento lunghezza riga
        int nFieldsCitizen =CitizenDTO.class.getDeclaredFields().length - 2;
        int nFieldsAddress = Address.class.getDeclaredFields().length;
        int lineLength = nFieldsCitizen + nFieldsAddress;



        for(String[] row: rows) {

            // Se sono presenti tutti i campi
            if (row.length == lineLength) {

                Citizen newCitizen = citizenMapper.mapCSVRowToCitizen(row);


                // Riga rappresenta un citizen con dati non validi
                if(newCitizen == null)
                    continue;


                // Controllo se esiste già un citizen con stesso SSN
                boolean citizenExist = citizenRepository.existsCitizenBySsn(newCitizen.getSsn());
                if (citizenExist)
                    continue;


                // Altrimenti aggiungo all'array dei citizen da salvare nel db
                batchCitizen.add(newCitizen);
            }
        }

        // Salvataggio in batch
        List<Citizen> createdCitizens = citizenRepository.saveAll(batchCitizen);

        /** Finalizzazione creazione cittadini
         * Notare la gestione locale dell'eccezione e il non intervento dell'handler globale
         * in modo da continuare con l'iterazione successiva (prossimo cittadino da salvare)
         * in caso di errore
         */
        for (Citizen citizen : createdCitizens) {

            String userID_created;

            // per ogni utente provo a fare la chiamata a loginMS
            try {
                userID_created = APICALL_createUser(citizen);
            } catch(WebClientResponseException e) {
                // si continua con il cittadino successivo da salvare
                continue;
            }

            // In caso di chiamata API correttamente eseguita
            citizen.setUserId(userID_created);
            citizenRepository.save(citizen);
            citizenIDs.add(citizen.getId());

        }

        return citizenIDs;
    }

    public List<String> getAllCitizensID() {

        List<String> allIDs = new ArrayList<>();
        List<Citizen> allCitizens = citizenRepository.findAll();

        for(Citizen citizen: allCitizens)
            allIDs.add(citizen.getId());

        return allIDs;
    }



    private String APICALL_createUser(Citizen newCitizen) {

        CitizenRegistrationDTO citizenRegistrationDTO = new CitizenRegistrationDTO();

        citizenRegistrationDTO.setCitizenId(newCitizen.getId());
        citizenRegistrationDTO.setEmail(newCitizen.getEmail());

        final String jwtToken = jwtUtilities.generateToken();



        return createUserWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/citizen_registration").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(citizenRegistrationDTO),CitizenRegistrationDTO.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError( error -> {updateDb(newCitizen);})
                .block();
    }



    private void updateDb(Citizen citizen) {
        citizenRepository.delete(citizen);
    }





}
