package it.smartcitywastemanagement.citizenms.service;


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
import it.smartcitywastemanagement.citizenms.utility.CSVProcessingUtility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    



    public String addNewCitizen(Citizen citizen) throws DuplicateKeyException, WebClientResponseException {

        // salvataggio citizen
        Citizen newCitizen = citizenRepository.save(citizen);

        // chiamata all'API di LOGINMS per creare lo user per l'accesso al sistema
        String userID_created = APICALL_createUser(newCitizen);

        // inserimento dello user ID appena creato
        newCitizen.setUser_id(userID_created);

        // update del cittadino
        citizenRepository.save(newCitizen);


        return newCitizen.getId();
    }


    public List<Citizen> findAllCitizens() {
        return citizenRepository.findAll();
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

        // Costruzione dell'array di IDs creati
        for (Citizen citizen : createdCitizens) {
            citizenIDs.add(citizen.getId());
        }

        return citizenIDs;
    }



    private String APICALL_createUser(Citizen newCitizen) {

        /*CitizenRegistrationDTO citizenRegistrationDTO = new CitizenRegistrationDTO();

        citizenRegistrationDTO.setEmail(newCitizen.getEmail());


        return createUserWebClient.post()
                .uri("/citizen_registration/{citizenID}",newCitizen.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(citizenRegistrationDTO),CitizenRegistrationDTO.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();*/
        return null;
    }






}
