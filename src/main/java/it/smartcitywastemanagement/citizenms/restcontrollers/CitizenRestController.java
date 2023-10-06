package it.smartcitywastemanagement.citizenms.restcontrollers;

import com.opencsv.exceptions.CsvException;
import it.smartcitywastemanagement.citizenms.domain.Citizen;
import it.smartcitywastemanagement.citizenms.dto.CitizenDTO;
import it.smartcitywastemanagement.citizenms.dto.ResponseDTO;
import it.smartcitywastemanagement.citizenms.exceptions.CitizenNotFoundException;
import it.smartcitywastemanagement.citizenms.exceptions.FileNotValidException;
import it.smartcitywastemanagement.citizenms.mappers.CitizenMapper;
import it.smartcitywastemanagement.citizenms.service.ManageCitizenService;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value="/api/citizen")
public class CitizenRestController {

    @Autowired
    private CitizenMapper citizenMapper;

    @Autowired
    private ManageCitizenService manageCitizenService;


    /*-----
    API PER AGGIUNTA CITTADINI
     -----*/

    @RequestMapping(value="/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> addCitizen(@Valid  @RequestBody CitizenDTO citizenDTO) throws DuplicateKeyException, WebClientResponseException {


        Citizen citizen = citizenMapper.toCitizen(citizenDTO);

        String createdId = manageCitizenService.addNewCitizen(citizen);

        return new ResponseEntity<>(
                new ResponseDTO("Citizen added successfully", createdId),
                HttpStatus.CREATED);
    }

    @RequestMapping(value="/add/upload", method=RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> addCitizensUsingFile(@RequestParam("file")MultipartFile file) throws CsvException, IOException, FileNotValidException {

        // Controllo preliminare file
        String filename = file.getOriginalFilename();
        String fileExtension = FilenameUtils.getExtension(filename);

        if (file.isEmpty() || !fileExtension.equalsIgnoreCase("csv"))
            throw new FileNotValidException();

        List<String> createdCitizens = manageCitizenService.loadCitizensFromFile(file);

        return new ResponseEntity<>(
                new ResponseDTO("Added "+createdCitizens.size()+" new citizens", createdCitizens),
                HttpStatus.CREATED);
    }



    /*-----
    API PER RICERCA CITTADINI
     -----*/

    @RequestMapping(value="/", method = RequestMethod.GET)
    public ResponseEntity<List<CitizenDTO>> getAllCitizens() {

        List<Citizen> results = manageCitizenService.findAllCitizens();

        List<CitizenDTO> all_citizens = fromCitizenToDTOArray(results);

        if (all_citizens.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(all_citizens);

    }

    @RequestMapping(value="/{citizenID}", method = RequestMethod.GET)
    public CitizenDTO getCitizenById(@PathVariable String citizenID) throws CitizenNotFoundException {

        Citizen citizen = manageCitizenService.findCitizenById(citizenID);

        CitizenDTO result = citizenMapper.toCitizenDTO(citizen);

        return result;
    }

    @RequestMapping(value="/user/{userID}", method=RequestMethod.GET)
    public ResponseEntity<ResponseDTO> getCitizenIdByUserId(@PathVariable String userID) throws CitizenNotFoundException{

        String citizenID = manageCitizenService.findCitizenIdByUserId(userID);

        return new ResponseEntity<>(
                new ResponseDTO("success",citizenID),
                HttpStatus.OK
        );
    }


    private List<CitizenDTO> fromCitizenToDTOArray(List<Citizen> entityCitizen) {
        List<CitizenDTO> result = new ArrayList<>();

        for(Citizen citizen: entityCitizen) {
            CitizenDTO citizenDTO = citizenMapper.toCitizenDTO(citizen);
            result.add(citizenDTO);
        }
        return result;
    }




}

