package com.hotelBooking.airBnB.strategy;

import com.hotelBooking.airBnB.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrappedPricingStrategy;

    @Override
    public BigDecimal calculateFinalPrice(Inventory inventory) {
        BigDecimal price = wrappedPricingStrategy.calculateFinalPrice(inventory);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), inventory.getDate())+1;
        if(days<=7){
            price = price.multiply(BigDecimal.valueOf(1.5));
        }
        return price;
    }
}
