package com.hotelBooking.airBnB.repository;

import com.hotelBooking.airBnB.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}