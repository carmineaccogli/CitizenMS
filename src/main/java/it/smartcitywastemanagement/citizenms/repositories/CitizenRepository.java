package it.smartcitywastemanagement.citizenms.repositories;

import it.smartcitywastemanagement.citizenms.domain.Citizen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CitizenRepository extends MongoRepository<Citizen, String> {

    List<Citizen> findAll();

    Optional<Citizen> findCitizenById(String citizenID);

    boolean existsCitizenBySsn(String ssn);

    Optional<Citizen> findByUserId(String userId);
}
