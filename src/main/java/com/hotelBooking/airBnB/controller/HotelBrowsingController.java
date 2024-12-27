package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.constants.AppConstants;
import com.hotelBooking.airBnB.dto.HotelDTO;
import com.hotelBooking.airBnB.dto.HotelInfoDTO;
import com.hotelBooking.airBnB.dto.HotelSearchRequestDTO;
import com.hotelBooking.airBnB.service.BrowsingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/browseHotel")
@RequiredArgsConstructor
@Slf4j
public class HotelBrowsingController {

    private static final String CLASS_NAME = HotelBrowsingController.class.getSimpleName();

    private final BrowsingService browsingService;

    @PostMapping("/search")
    public ResponseEntity<Page<HotelDTO>> searchHotel(@RequestBody HotelSearchRequestDTO hotelSearchRequestDTO){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "searchHotel");
        Page<HotelDTO> hotels = browsingService.searchHotelBasedOnDates(hotelSearchRequestDTO);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelInfoDTO> getHotelAndRooms(@PathVariable Long hotelId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "getHotelAndRooms");
        HotelInfoDTO hotelInfoDTO = browsingService.getHotelAndRoomsById(hotelId);
        return ResponseEntity.ok(hotelInfoDTO);
    }
}
