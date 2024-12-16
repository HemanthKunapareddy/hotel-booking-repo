package com.hotelBooking.airBnB.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactInfo {
    private long id;
    private String email;
    private String address;
    private String location;
    private String phoneNumber;
}
