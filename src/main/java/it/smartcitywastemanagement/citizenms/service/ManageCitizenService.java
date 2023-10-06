package it.smartcitywastemanagement.citizenms.service;

import com.opencsv.exceptions.CsvException;
import it.smartcitywastemanagement.citizenms.domain.Citizen;
import it.smartcitywastemanagement.citizenms.exceptions.CitizenNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ManageCitizenService {

    String addNewCitizen(Citizen citizen);

    List<Citizen> findAllCitizens();

    Citizen findCitizenById(String citizenID) throws CitizenNotFoundException;

    List<String> loadCitizensFromFile(MultipartFile file) throws CsvException, IOException;
    String findCitizenIdByUserId(String userID) throws CitizenNotFoundException;
}
