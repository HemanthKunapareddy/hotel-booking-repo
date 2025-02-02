package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.entity.Booking;
import com.hotelBooking.airBnB.entity.User;
import com.hotelBooking.airBnB.repository.BookingRepository;
import com.hotelBooking.airBnB.util.UserUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final BookingRepository bookingRepository;

    public String getCheckoutSession(Booking booking, String successURL, String failureURL){

        User user = UserUtil.getCurrentUser();

        try{
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setName(user.getName())
                    .setAddress(CustomerCreateParams.Address.builder()
                            .setLine1("10-85/2, MIG - 562")
                            .setLine2("Near Post Office")
                            .setCity("Hyderabad")
                            .setState("Telangana")
                            .setCountry("India")
                            .setPostalCode("500032")
                            .build())
                    .build();

            Customer customer = Customer.create(customerCreateParams);

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successURL)
                    .setCancelUrl(failureURL)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotelId().getHotelName() +" : "+ booking.getRoomId().getRoomType())
                                                                    .setDescription("Booking ID: "+booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionParams);

            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);
            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
