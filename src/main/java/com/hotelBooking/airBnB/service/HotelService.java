package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.dto.HotelDTO;

import java.util.List;

public interface HotelService {

    HotelDTO createHotel(HotelDTO hotelDTO);

    HotelDTO getHotelById(Long hotelId);

    List<HotelDTO> getAllHotels();

    HotelDTO updateHotelById(Long hotelId, HotelDTO hotelDTO);

    void setHotelToActive(Long hotelId);

    void deleteHotelById(Long hotelId);
}
