package com.hotelBooking.airBnB.strategy;

import com.hotelBooking.airBnB.entity.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculateFinalPrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
