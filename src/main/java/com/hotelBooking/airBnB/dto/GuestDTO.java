package com.hotelBooking.airBnB.dto;

import com.hotelBooking.airBnB.entity.User;
import com.hotelBooking.airBnB.enums.Gender;
import lombok.Data;

@Data
public class GuestDTO {
    private String name;
    private Gender gender;
}
