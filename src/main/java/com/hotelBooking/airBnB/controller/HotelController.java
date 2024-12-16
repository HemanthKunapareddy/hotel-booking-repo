package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.dto.HotelDTO;
import com.hotelBooking.airBnB.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private static final String CLASS_NAME = HotelController.class.getName();

    @Autowired
    private HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO) {
        log.info("IN {} CLASS : IN {} METHOD : {}", CLASS_NAME, "createHotel", "Creating Hotel!!");
        HotelDTO hotel = hotelService.createHotel(hotelDTO);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> getHotel(@PathVariable long hotelId) {
        log.info("IN {} CLASS : IN {} METHOD : {}", CLASS_NAME, "getHotel", "Fetching Hotel Details");
        HotelDTO hotel = hotelService.getHotel(hotelId);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping
    public ResponseEntity<?> getAllHotel() {
        log.info("IN {} CLASS : IN {} METHOD : {}", CLASS_NAME, "getAllHotels", "Fetching Hotel Details");
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }
}
