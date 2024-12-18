package com.hotelBooking.airBnB.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class RoomDTO {
    private long id;

    @NotBlank(message = "Room type should note be blank. Provide some value!!")
    private String roomType;

    @Positive(message = "Price should not be a negative value!!")
    private BigDecimal basePrice;
    private String[] photos;
    private String[] amenities;
    private int totalCount;

    @Positive
    @Min(value = 1, message = "Capacity should be minimum of 1!!")
    private int capacity;
}
