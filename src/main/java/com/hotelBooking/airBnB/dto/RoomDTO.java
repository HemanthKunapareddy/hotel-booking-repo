package com.hotelBooking.airBnB.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class RoomDTO {
    private long id;
    private String roomType;
    private BigDecimal basePrice;
    private String[] photos;
    private String[] amenities;
    private int totalCount;
    private int capacity;
}
