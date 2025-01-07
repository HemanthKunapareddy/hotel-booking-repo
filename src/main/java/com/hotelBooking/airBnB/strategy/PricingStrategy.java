package com.hotelBooking.airBnB.strategy;

import com.hotelBooking.airBnB.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculateFinalPrice(Inventory inventory);
}
