package com.hotelBooking.airBnB.controller;

import com.hotelBooking.airBnB.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final BookingService bookingService;

    @Value("${Stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payments")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            System.out.println("Entered webhook controller");
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("✅ Received event: " + event.getType());

            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
