package com.hotelBooking.airBnB.dto;

import com.hotelBooking.airBnB.entity.ContactInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HotelDTO {

    private long id;

    @NotBlank(message = "Hotel name Should not be Blank!!")
    private String hotelName;

    @NotBlank(message = "City should not be Blank!!")
    private String city;

    @NotNull(message = "Contact info should not be null!!")
    private ContactInfo contactInfo;

    private String[] photos;

    private String[] amenities;

    private boolean active;
}
