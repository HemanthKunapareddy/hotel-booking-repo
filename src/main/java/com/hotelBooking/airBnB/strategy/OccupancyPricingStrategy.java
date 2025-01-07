package com.hotelBooking.airBnB.strategy;

import com.hotelBooking.airBnB.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrappedPricingStrategy;

    @Override
    public BigDecimal calculateFinalPrice(Inventory inventory) {
        BigDecimal price = wrappedPricingStrategy.calculateFinalPrice(inventory);
        double occupancy = (double) inventory.getBookedCount() / inventory.getTotalCount();
        if(occupancy>0.8){
            price = price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
