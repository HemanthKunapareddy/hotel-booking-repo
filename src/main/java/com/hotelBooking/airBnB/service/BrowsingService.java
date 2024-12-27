package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.dto.HotelDTO;
import com.hotelBooking.airBnB.dto.HotelInfoDTO;
import com.hotelBooking.airBnB.dto.HotelSearchRequestDTO;
import org.springframework.data.domain.Page;

public interface BrowsingService {

    Page<HotelDTO> searchHotelBasedOnDates(HotelSearchRequestDTO searchRequestDTO);

    HotelInfoDTO getHotelAndRoomsById(Long hotelId);
}
