package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.dto.HotelDTO;

import java.util.List;

public interface HotelService {

    HotelDTO createHotel(HotelDTO hotelDTO);

    HotelDTO getHotel(long hotelId);

    List<HotelDTO> getAllHotels();
}
