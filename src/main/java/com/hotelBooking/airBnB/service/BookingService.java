package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.dto.BookingDTO;
import com.hotelBooking.airBnB.dto.BookingRequest;
import com.hotelBooking.airBnB.dto.GuestDTO;
import com.stripe.model.Event;

import java.util.List;

public interface BookingService {

    BookingDTO initializeBooking(BookingRequest bookingRequest);

    BookingDTO addGuestsToBooking(Long bookingId, List<GuestDTO> guests) throws IllegalAccessException;

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelpayment(Long bookingId);
}
