package it.smartcitywastemanagement.citizenms.dto;

import com.opencsv.bean.CsvBindByName;
import it.smartcitywastemanagement.citizenms.domain.Address;
import it.smartcitywastemanagement.citizenms.validators.SSNFormat;
import it.smartcitywastemanagement.citizenms.validators.ValidAddress;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;

public class CitizenDTO {


    private String id;


    @NotBlank(message = "{NotBlank.citizen.name}")
    private String name;



    @NotBlank(message = "{NotBlank.citizen.surname}")
    private String surname;


    @NotBlank(message ="{NotBlank.citizen.ssn}")
    @SSNFormat(message= "{SSNFormat.citizen.ssn}")
    private String ssn;


    @NotBlank(message = "{NotBlank.citizen.email}")
    @Email(message = "{Email.citizen.email}")
    private String email;

    // private String user_id;


    @ValidAddress(message = "{ValidAddress.citizen.address}")
    private Address address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
