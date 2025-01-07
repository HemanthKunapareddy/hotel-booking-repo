package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.dto.*;
import com.hotelBooking.airBnB.entity.Hotel;
import com.hotelBooking.airBnB.entity.HotelMinPrice;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.repository.HotelMinPriceRepository;
import com.hotelBooking.airBnB.repository.HotelRepository;
import com.hotelBooking.airBnB.repository.InventoryRepository;
import com.hotelBooking.airBnB.service.BrowsingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrowsingServiceImpl implements BrowsingService {
    private final HotelMinPriceRepository hotelMinPriceRepository;

    private static final String CLASS_NAME = BrowsingServiceImpl.class.getSimpleName();

    private static final String IN_CLASS_METHOD = "In Class: {} Method: {}";

    private final HotelRepository hotelRepository;

    private final ModelMapper modelMapper;

    private final InventoryRepository inventoryRepository;

    @Override
    public Page<HotelDTO> searchHotelBasedOnDates(HotelSearchRequestDTO searchRequestDTO) {
        log.info(IN_CLASS_METHOD, CLASS_NAME, "searchHotelBasedOnDates");

        Pageable pageable = PageRequest.of(searchRequestDTO.getPage(), searchRequestDTO.getPageItems());
        Long days = ChronoUnit.DAYS.between(searchRequestDTO.getStartDate(), searchRequestDTO.getEndDate()) + 1;

        Page<Hotel> hotels = inventoryRepository.searchHotelInInventoryBasedOnDates(searchRequestDTO.getCity(),
                searchRequestDTO.getStartDate(), searchRequestDTO.getEndDate(), searchRequestDTO.getRoomsCount(),
                days, pageable);

        return hotels.map((element) -> modelMapper.map(element, HotelDTO.class));
    }

    @Override
    public Page<HotelMinDTO> searchHotelMinPricesBasedOnDates(HotelSearchRequestDTO searchRequestDTO) {
        Pageable pageable = PageRequest.of(searchRequestDTO.getPage(), searchRequestDTO.getPageItems());

        return hotelMinPriceRepository.searchHotelMinPricesBasedOnDates(searchRequestDTO.getCity(),
                searchRequestDTO.getStartDate(), searchRequestDTO.getEndDate(), pageable);
    }


    @Override
    public HotelInfoDTO getHotelAndRoomsById(Long hotelId) {
        log.info(IN_CLASS_METHOD, CLASS_NAME, "getHotelAndRoomsById");

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id: " + hotelId + " not found"));

        List<RoomDTO> rooms = hotel
                .getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDTO.class))
                .toList();
        return new HotelInfoDTO(modelMapper.map(hotel, HotelDTO.class), rooms);
    }

}
