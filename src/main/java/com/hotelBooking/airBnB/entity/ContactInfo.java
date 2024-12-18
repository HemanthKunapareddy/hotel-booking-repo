package com.hotelBooking.airBnB.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactInfo {
    private long id;

    @Email(message = "Email entered is not a valid one!")
    private String email;
    private String address;
    private String location;

    @Pattern(regexp = "^\\+?91[-.\\s]?[6789]\\d{9}$")
    private String phoneNumber;
}
