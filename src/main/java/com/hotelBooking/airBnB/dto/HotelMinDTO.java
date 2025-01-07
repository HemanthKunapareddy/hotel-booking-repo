package com.hotelBooking.airBnB.dto;

import com.hotelBooking.airBnB.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelMinDTO {

    private Hotel hotel;
    private Double minPrice;
}
