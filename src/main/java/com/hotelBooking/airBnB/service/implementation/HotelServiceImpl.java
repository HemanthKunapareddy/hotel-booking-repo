package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.dto.HotelDTO;
import com.hotelBooking.airBnB.entity.Hotel;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.repository.HotelRepository;
import com.hotelBooking.airBnB.repository.UserRepository;
import com.hotelBooking.airBnB.service.HotelService;
import com.hotelBooking.airBnB.service.InventoryService;
import com.hotelBooking.airBnB.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final HotelRepository hotelRepository;

    private final InventoryService inventoryService;

    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating new hotel with hotel name {}.", hotelDTO.getHotelName());
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        hotel.setActive(false);
        hotel.setOwner(UserUtil.getCurrentUser());
        hotel = hotelRepository.save(hotel);
        log.info("Hotel {} saved to database!", hotel.getHotelName());
        return modelMapper.map(hotel, HotelDTO.class);
    }

    public HotelDTO getHotelById(Long hotelId) {
        log.info("Fetching hotel details for hotel id {}", hotelId);
        Optional<Hotel> hotel = hotelRepository.findById(hotelId);
        return hotel.map(htl -> modelMapper.map(htl, HotelDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not Found with the id:" + hotelId));
    }

    public List<HotelDTO> getAllHotels() {
        log.info("Fetch all hotels!!");
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels
                .stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelDTO updateHotelById(Long hotelId, HotelDTO hotelDTO) {
        log.info("Updating hotel with id: {} with new hotel details.", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("No hotel found with id: "+ hotelId));
        modelMapper.map(hotelDTO, hotel);
        hotel.setId(hotelId);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public void setHotelToActive(Long hotelId) {
        log.info("Setting hotel with Id: {} to active", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("No hotel found with id: "+ hotelId));
        hotel.setActive(true);
        hotelRepository.save(hotel);
        hotel.getRooms().forEach(inventoryService::createInventoryForRoom);
    }

    public void deleteHotelById(Long hotelId) {
        log.info("Deleting hotel with Id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with the Id: " + hotelId));
        hotelRepository.delete(hotel);
    }
}
