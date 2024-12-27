package com.hotelBooking.airBnB.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelInfoDTO {

    private HotelDTO hotel;
    private List<RoomDTO> rooms;
}
