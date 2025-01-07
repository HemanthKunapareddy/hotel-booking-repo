package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.constants.AppConstants;
import com.hotelBooking.airBnB.dto.BookingDTO;
import com.hotelBooking.airBnB.dto.BookingRequest;
import com.hotelBooking.airBnB.dto.GuestDTO;
import com.hotelBooking.airBnB.entity.*;
import com.hotelBooking.airBnB.enums.BookingStatus;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.repository.*;
import com.hotelBooking.airBnB.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;

    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    private static final String CLASS_NAME = BookingServiceImpl.class.getSimpleName();

    @Override
    @Transactional
    public BookingDTO initializeBooking(BookingRequest bookingRequest) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "initializeBooking");

        Hotel hotel = hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with Id: " + bookingRequest.getHotelId() + " not found"));

        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room with Id: " + bookingRequest.getRoomId() + " not found"));

        List<Inventory> inventories = inventoryRepository.getInventories(bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());

        long days = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventories.size() != days) {
            throw new IllegalStateException("Room with days: " + bookingRequest.getRoomsCount() + " vacancy not found");
        }

        for (Inventory inventory : inventories) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventories);

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User with Id: 1 not found"));

        Booking booking = Booking.builder()
                .hotelId(hotel)
                .roomId(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .bookingStatus(BookingStatus.RESERVED)
                .userId(user)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    public BookingDTO addGuestsToBooking(Long bookingId, List<GuestDTO> guests) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "addGuestsToBooking");
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with Id: " + bookingId + " not found"));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: 1 not found"));

        if (isBookingExpired(booking)) {
            throw new IllegalStateException("Booking Expired!!");
        }

        if (!booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
            throw new IllegalStateException("Booking status is not RESERVED, cannot proceed booking");
        }

        for (GuestDTO guest : guests) {
            Guest gst = modelMapper.map(guest, Guest.class);
            gst.setUserId(user);
            gst = guestRepository.save(gst);
            booking.getGuests().add(gst);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }

    public boolean isBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isEqual(LocalDateTime.now());
    }
}
