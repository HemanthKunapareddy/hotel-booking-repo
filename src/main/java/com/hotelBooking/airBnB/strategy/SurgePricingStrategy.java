package com.hotelBooking.airBnB.strategy;

import com.hotelBooking.airBnB.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{

    private final PricingStrategy wrappedPricingStrategy;

    @Override
    public BigDecimal calculateFinalPrice(Inventory inventory) {
        BigDecimal price = wrappedPricingStrategy.calculateFinalPrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
