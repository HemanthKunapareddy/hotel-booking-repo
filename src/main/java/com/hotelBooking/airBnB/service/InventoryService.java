package com.hotelBooking.airBnB.service;

import com.hotelBooking.airBnB.entity.Room;

import java.time.LocalDate;

public interface InventoryService {

    void createInventoryForRoom(Room room);

    void deleteInventoryForRoom(Room room);
}
