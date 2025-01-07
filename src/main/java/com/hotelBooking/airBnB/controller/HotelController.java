package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.constants.AppConstants;
import com.hotelBooking.airBnB.dto.HotelDTO;
import com.hotelBooking.airBnB.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private static final String CLASS_NAME = HotelController.class.getName();

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "createHotel");
        HotelDTO hotel = hotelService.createHotel(hotelDTO);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long hotelId) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "getHotel");
        HotelDTO hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping
    public ResponseEntity<?> getAllHotel() {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "getAllHotels");
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotelById(@Valid @RequestBody HotelDTO hotelDTO,
                                                    @PathVariable Long hotelId) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "updateHotelById");
        HotelDTO hotel = hotelService.updateHotelById(hotelId, hotelDTO);
        return ResponseEntity.ok(hotel);
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<?> setHotelToActive(@PathVariable Long hotelId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "setHotelToActive");
        hotelService.setHotelToActive(hotelId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotelById(@PathVariable Long hotelId) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "deleteHotelById");
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }
}
