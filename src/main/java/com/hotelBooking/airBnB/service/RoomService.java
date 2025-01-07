package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.dto.RoomDTO;

import java.util.List;

public interface RoomService {

    RoomDTO createRoomInHotel(long hotelId, RoomDTO roomDTO);

    RoomDTO getRoomFromHotel(long hotelId, long roomId);

    RoomDTO getRoomById(long roomId);

    List<RoomDTO> getAllRoomsFromHotel(long hotelId);

    void deleteRoomById(long roomId);

}
