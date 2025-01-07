package com.hotelBooking.airBnB.strategy;

import com.hotelBooking.airBnB.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrappedPricingStrategy;

    @Override
    public BigDecimal calculateFinalPrice(Inventory inventory) {
        BigDecimal price = wrappedPricingStrategy.calculateFinalPrice(inventory);
        boolean isHoliday = true;
        if(isHoliday){
            price = price.multiply(BigDecimal.valueOf(1.5));
        }
        return price;
    }
}
