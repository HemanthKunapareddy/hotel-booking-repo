package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.entity.Inventory;
import com.hotelBooking.airBnB.strategy.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
}
