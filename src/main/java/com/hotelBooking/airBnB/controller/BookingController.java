package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.constants.AppConstants;
import com.hotelBooking.airBnB.dto.BookingDTO;
import com.hotelBooking.airBnB.dto.BookingRequest;
import com.hotelBooking.airBnB.dto.GuestDTO;
import com.hotelBooking.airBnB.dto.PaymentDTO;
import com.hotelBooking.airBnB.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private static final String CLASS_NAME = BookingController.class.getSimpleName();

    private final BookingService bookingService;

    @PostMapping("/initialize")
    public ResponseEntity<BookingDTO> initiateBooking(@RequestBody BookingRequest bookingRequest){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "initiateBooking");

        BookingDTO booking = bookingService.initializeBooking(bookingRequest);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDTO> addGuestsToBooking(@PathVariable Long bookingId,
                                                         @RequestBody List<GuestDTO> guests) throws IllegalAccessException {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "addGuests");

        BookingDTO booking = bookingService.addGuestsToBooking(bookingId, guests);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/initializePayment")
    public ResponseEntity<PaymentDTO> initializePayment(@PathVariable Long bookingId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "initializePayment");

        String sessionURL = bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(new PaymentDTO(sessionURL));
    }

    @PostMapping("/{bookingId}/cancelPayment")
    public ResponseEntity<PaymentDTO> cancelPayment(@PathVariable Long bookingId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "initializePayment");
        bookingService.cancelPayment(bookingId);
        return ResponseEntity.noContent().build();
    }

}
