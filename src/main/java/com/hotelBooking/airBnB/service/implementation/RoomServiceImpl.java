package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.dto.RoomDTO;
import com.hotelBooking.airBnB.entity.Hotel;
import com.hotelBooking.airBnB.entity.Room;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.repository.HotelRepository;
import com.hotelBooking.airBnB.repository.RoomRepository;
import com.hotelBooking.airBnB.service.InventoryService;
import com.hotelBooking.airBnB.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final ModelMapper modelMapper;

    private final RoomRepository roomRepository;

    private final HotelRepository hotelRepository;

    private final InventoryService inventoryService;

    @Override
    public RoomDTO createRoomInHotel(long hotelId, RoomDTO roomDTO) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id: " + hotelId + " not found!!"));
        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        log.info("Creating a Room with Id: {} in Hotel with Id: {}", room.getId(), room.getHotel().getId());

        if (hotel.isActive()) {
            inventoryService.createInventoryForRoom(room);
        }

        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public RoomDTO getRoomFromHotel(long hotelId, long roomId) {
        log.info("Fetching Room details with room id: {} in Hotel with id: {}", roomId, hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id: " + hotelId + "notFound!!"));
        Room room = null;
        try {
            room = hotel.getRooms()
                    .stream()
                    .filter(room1 -> room1.getId() == roomId)
                    .toList()
                    .getFirst();
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Room wth id: " + roomId + " not found in hotel: " + hotelId);
        }
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public RoomDTO getRoomById(long roomId) {
        log.info("Fetching Room details with room id: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with Id: " + roomId + "not found!!"));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsFromHotel(long hotelId) {
        log.info("Fetching all rooms details from hotel with id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id: " + hotelId + "notFound!!"));
        return hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public void deleteRoomById(long roomId) {
        log.info("Deleting room with id: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with Id: " + roomId + "not found!!"));
        inventoryService.deleteInventoryForRoom(room);
        roomRepository.delete(room);
    }
}
