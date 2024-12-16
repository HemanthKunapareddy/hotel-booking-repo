package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.dto.HotelDTO;
import com.hotelBooking.airBnB.entity.Hotel;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.repository.HotelRepository;
import com.hotelBooking.airBnB.service.HotelService;
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

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HotelRepository hotelRepository;

    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating new hotel with hotel name {}.", hotelDTO.getHotelName());
        Hotel hotel = modelMapper.map(hotelDTO, Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);
        log.info("Hotel {} saved to database!", hotel.getHotelName());
        return modelMapper.map(hotel, HotelDTO.class);
    }

    public HotelDTO getHotel(long hotelId){
        log.info("Fetching hotel details for hotel id {}", hotelId);
        Optional<Hotel> hotel = hotelRepository.findById(hotelId);
        return hotel.map(htl -> modelMapper.map(htl, HotelDTO.class))
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not Found with id:"+hotelId));
    }

    public List<HotelDTO> getAllHotels(){
        log.info("Fetch all hotels!!");
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels
                .stream()
                .map(hotel -> modelMapper.map(hotel, HotelDTO.class))
                .collect(Collectors.toList());
    }
}
