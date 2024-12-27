package com.hotelBooking.airBnB.dto;

import com.hotelBooking.airBnB.entity.User;
import com.hotelBooking.airBnB.enums.Gender;
import lombok.Data;

@Data
public class GuestDTO {
    private long id;
    private User userId;
    private String name;
    private Gender gender;
}
