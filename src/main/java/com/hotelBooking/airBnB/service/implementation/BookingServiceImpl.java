package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.constants.AppConstants;
import com.hotelBooking.airBnB.dto.BookingDTO;
import com.hotelBooking.airBnB.dto.BookingRequest;
import com.hotelBooking.airBnB.dto.GuestDTO;
import com.hotelBooking.airBnB.entity.*;
import com.hotelBooking.airBnB.enums.BookingStatus;
import com.hotelBooking.airBnB.exceptions.ResourceNotFoundException;
import com.hotelBooking.airBnB.exceptions.UnAuthorisedException;
import com.hotelBooking.airBnB.repository.*;
import com.hotelBooking.airBnB.service.BookingService;
import com.hotelBooking.airBnB.service.CheckoutService;
import com.hotelBooking.airBnB.service.PricingStrategyService;
import com.hotelBooking.airBnB.util.UserUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hotelBooking.airBnB.enums.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private final PricingStrategyService pricingStrategyService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final CheckoutService checkoutService;

    @Value("${frontend.url}")
    private String frontendURL;

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

        System.out.println("is inventories empty :" + inventories.isEmpty());

        long days = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventories.size() != days) {
            throw new ResourceNotFoundException("Room with days: " + bookingRequest.getRoomsCount() + " vacancy not found");
        }

        for (Inventory inventory : inventories) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventories);

        User user = UserUtil.getCurrentUser();

        BigDecimal priceForARoom = pricingStrategyService.calculatePrice(inventories);
        BigDecimal totalPrice = priceForARoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = Booking.builder()
                .hotelId(hotel)
                .roomId(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .bookingStatus(RESERVED)
                .userId(user)
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        bookingDTO.setRoomsCount(bookingRequest.getRoomsCount());
        return bookingDTO;
    }

    @Override
    @Transactional
    public BookingDTO addGuestsToBooking(Long bookingId, List<GuestDTO> guests) {
        log.info(AppConstants.IN_CLASS_METHOD, CLASS_NAME, "addGuestsToBooking");
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with Id: " + bookingId + " not found"));
        User user = UserUtil.getCurrentUser();
        if (!user.equals(booking.getUserId())) {
            throw new UnAuthorisedException("User accessing the booking is Forbidden");
        }

        if (isBookingExpired(booking)) {
            throw new IllegalStateException("Booking Expired!!");
        }

        if (!booking.getBookingStatus().equals(RESERVED)) {
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

        return mapGuestsDTO(modelMapper.map(booking, BookingDTO.class));
    }

    @Override
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id: " + bookingId + " not found!"));

        User user = UserUtil.getCurrentUser();

        if (!user.equals(booking.getUserId())) {
            throw new UnAuthorisedException("User accessing the booking is Forbidden");
        }

        if (isBookingExpired(booking)) {
            throw new IllegalStateException("Booking Expired!!");
        }

        if (!booking.getBookingStatus().equals(GUESTS_ADDED)) {
            throw new IllegalStateException("Booking status is not RESERVED, cannot proceed booking");
        }

        String sessionURL = checkoutService.getCheckoutSession(booking,
                frontendURL + "/success.html",
                frontendURL + "/failure.html");

        booking.setBookingStatus(PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionURL;
    }

    @Override
    @Transactional
    public ResponseEntity<?> capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session is invalid");
            }

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: " + sessionId));

            if(booking.getBookingStatus().equals(BookingStatus.CONFIRMED)){
                return ResponseEntity.ok().build();
            }
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            List<Inventory> inventoryList = inventoryRepository.findAndLockReservedInventory(booking.getRoomId().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            inventoryList.forEach(inventory -> {
                inventory.setReservedCount(inventory.getReservedCount() - booking.getRoomsCount());
                inventory.setBookedCount(inventory.getBookedCount() + booking.getRoomsCount());
                inventoryRepository.save(inventory);
            });

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
            return ResponseEntity.ok().build();
        } else {
            log.warn("Unhandled event type: {}", event.getType());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @Transactional
    public void cancelPayment(Long bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with Id: " + bookingId + " not found"));
        User user = UserUtil.getCurrentUser();
        if (!user.equals(booking.getUserId())) {
            throw new UnAuthorisedException("User accessing the booking is Forbidden");
        }

        if (!booking.getBookingStatus().equals(CONFIRMED)) {
            throw new IllegalStateException("Booking status is not Confirmed, cannot proceed Cancellation");
        }

        booking.setBookingStatus(CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoomId().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoomId().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        List<Inventory> inventoryList = inventoryRepository.findAndLockReservedInventory(booking.getRoomId().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryList.forEach(inventory -> {
            inventory.setBookedCount(inventory.getBookedCount() - booking.getRoomsCount());
            inventoryRepository.save(inventory);
        });
    }

    public boolean isBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public BookingDTO mapGuestsDTO(BookingDTO bookingDTO) {
        Set<GuestDTO> guestDTOSet = bookingDTO.getGuests()
                .stream()
                .map(gst -> modelMapper.map(gst, GuestDTO.class))
                .collect(Collectors.toSet());
        bookingDTO.setGuests(guestDTOSet);
        return bookingDTO;
    }

}
