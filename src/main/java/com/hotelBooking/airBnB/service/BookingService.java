package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.dto.BookingDTO;
import com.hotelBooking.airBnB.dto.BookingRequest;
import com.hotelBooking.airBnB.dto.GuestDTO;

import java.util.List;

public interface BookingService {

    BookingDTO initializeBooking(BookingRequest bookingRequest);

    BookingDTO addGuestsToBooking(Long bookingId, List<GuestDTO> guests);
}
