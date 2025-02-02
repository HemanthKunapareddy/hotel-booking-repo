package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.entity.Inventory;
import com.hotelBooking.airBnB.strategy.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingStrategyService {

    public BigDecimal getFinalPrice(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);

        return pricingStrategy.calculateFinalPrice(inventory);
    }

    public BigDecimal calculatePrice(List<Inventory> inventoryList){
        return inventoryList.stream()
                .map(this::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
