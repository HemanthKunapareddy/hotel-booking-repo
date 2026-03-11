package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.constants.AppConstants;
import com.hotelBooking.airBnB.dto.RoomDTO;
import com.hotelBooking.airBnB.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private static final String CLASS_NAME = RoomController.class.getName();

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoomInHotel(@Valid @RequestBody RoomDTO roomDTO, @PathVariable Long hotelId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "createRoomInHotel");
        RoomDTO room = roomService.createRoomInHotel(hotelId, roomDTO);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomFromHotel(@PathVariable Long hotelId, @PathVariable Long roomId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "getRoomFromHotel");
        RoomDTO room = roomService.getRoomFromHotel(hotelId, roomId);
        return ResponseEntity.ok(room);
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRoomsFromHotel(@PathVariable Long hotelId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "getAllRoomsFromHotel");
        List<RoomDTO> rooms = roomService.getAllRoomsFromHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoomById(@PathVariable Long roomId){
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "deleteRoomById");
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

}
