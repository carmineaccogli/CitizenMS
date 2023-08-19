package it.smartcitywastemanagement.citizenms.mappers;


import it.smartcitywastemanagement.citizenms.domain.Address;
import it.smartcitywastemanagement.citizenms.domain.Citizen;
import it.smartcitywastemanagement.citizenms.dto.CitizenDTO;
import it.smartcitywastemanagement.citizenms.service.ManageCitizenServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CitizenMapper {

    @Autowired
    private Validator validator;


    public Citizen toCitizen(CitizenDTO citizenDTO) {

        Citizen citizen = new Citizen();

        citizen.setName(citizenDTO.getName());
        citizen.setSurname(citizenDTO.getSurname());
        citizen.setSsn(citizenDTO.getSsn());
        citizen.setEmail(citizenDTO.getEmail());
        citizen.setAddress(citizenDTO.getAddress());

        return citizen;
    }


    public CitizenDTO toCitizenDTO(Citizen citizen) {

        CitizenDTO citizenDTO = new CitizenDTO();

        citizenDTO.setId(citizen.getId());
        citizenDTO.setName(citizen.getName());
        citizenDTO.setSurname(citizen.getSurname());
        citizenDTO.setSsn(citizen.getSsn());
        citizenDTO.setEmail(citizen.getEmail());
        citizenDTO.setAddress(citizen.getAddress());
        return citizenDTO;
    }

    public Citizen mapCSVRowToCitizen(String[] row) {
        CitizenDTO citizenDTO = new CitizenDTO();

        // Creazione del DTO
        citizenDTO.setName(row[0]);
        citizenDTO.setSurname(row[1]);
        citizenDTO.setSsn(row[2]);
        citizenDTO.setEmail(row[3]);
        Address address = new Address(row[4], row[5], row[6], row[7]);
        citizenDTO.setAddress(address);

        // Validazione del DTO
        Set<ConstraintViolation<CitizenDTO>> violations = validator.validate(citizenDTO);
        if (!violations.isEmpty()) {
            return null;
        }

        Citizen citizen = toCitizen(citizenDTO);
        return citizen;
    }
}
