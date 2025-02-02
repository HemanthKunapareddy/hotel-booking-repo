package com.hotelBooking.airBnB.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    public StripeConfig(@Value("${Stripe.secret}") String secret){
        Stripe.apiKey = secret;
    }

}
