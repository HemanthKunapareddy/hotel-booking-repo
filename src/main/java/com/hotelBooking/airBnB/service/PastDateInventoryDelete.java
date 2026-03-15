package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.entity.Hotel;
import com.hotelBooking.airBnB.entity.Inventory;
import com.hotelBooking.airBnB.repository.HotelRepository;
import com.hotelBooking.airBnB.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PastDateInventoryDelete {

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void deletePreviousDatedInventories() {
        log.info("Entered delete Previous Dated Inventories Method!!");
        int page = 0;
        int pageSize = 100;

        while (true) {
            Page<Hotel> hotels = hotelRepository.findAll(PageRequest.of(page, pageSize));
            if (hotels.isEmpty()) break;
            hotels.forEach(this::deleteInventory);
            page++;
        }
    }

    @Transactional
    public void deleteInventory(Hotel hotel) {
        log.info("Entered method Delete Inventory!!");
        LocalDate currentDate = LocalDate.now();
        List<Inventory> inventories = inventoryRepository.findByHotelAndDateBefore(hotel, currentDate);

        inventoryRepository.deleteAll(inventories);
    }
}
