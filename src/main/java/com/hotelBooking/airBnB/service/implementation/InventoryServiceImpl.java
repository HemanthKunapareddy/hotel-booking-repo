package com.hotelBooking.airBnB.service.implementation;

import com.hotelBooking.airBnB.entity.Inventory;
import com.hotelBooking.airBnB.entity.Room;
import com.hotelBooking.airBnB.repository.InventoryRepository;
import com.hotelBooking.airBnB.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public void createInventoryForRoom(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; today.isBefore(endDate); today = today.plusDays(1)) {
            Inventory inventory = Inventory
                    .builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .price(room.getBasePrice())
                    .date(today)
                    .bookedCount(0)
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(10)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteInventoryForRoom(Room room) {
//        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByRoom(room);
    }
}
