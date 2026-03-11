package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.entity.Hotel;
import com.hotelBooking.airBnB.entity.HotelMinPrice;
import com.hotelBooking.airBnB.entity.Inventory;
import com.hotelBooking.airBnB.repository.HotelMinPriceRepository;
import com.hotelBooking.airBnB.repository.HotelRepository;
import com.hotelBooking.airBnB.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingUpdateService {

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final PricingStrategyService pricingStrategyService;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void updatePrices() {
        log.info("Updating prices for hotels");
        int page = 0;
        int size = 100;

        while (true) {
            Page<Hotel> hotels = hotelRepository.findAll(PageRequest.of(page, size));
            if (hotels.isEmpty()) break;
            hotels.forEach(this::updateHotelPrices);
            page++;
        }
    }

    public void updateHotelPrices(Hotel hotel) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        List<Inventory> inventories = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventories);

        updateHotelMinPrices(hotel, inventories);
    }

    public void updateInventoryPrices(List<Inventory> inventories) {
        inventories.forEach(inventory -> {
            BigDecimal price = pricingStrategyService.getFinalPrice(inventory);
            inventory.setPrice(price);
        });

        inventoryRepository.saveAll(inventories);
    }

    public void updateHotelMinPrices(Hotel hotel, List<Inventory> inventories) {
        Map<LocalDate, BigDecimal> prices = new HashMap<>();
        for (Inventory inventory : inventories) {
            BigDecimal minPrice = inventory.getPrice();
            LocalDate date = inventory.getDate();
            for (Inventory inventory1 : inventories) {
                if (date.isEqual(inventory1.getDate())) {
                    minPrice = inventory1.getPrice().min(minPrice);
                }
            }
            prices.put(date, minPrice);
        }

        List<HotelMinPrice> hotels = new ArrayList<>();
        prices.forEach((date, price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository
                    .findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setMinPrice(price);
            hotels.add(hotelMinPrice);
        });

        hotelMinPriceRepository.saveAll(hotels);
    }
}
