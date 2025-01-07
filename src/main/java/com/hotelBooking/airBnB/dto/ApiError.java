package com.hotelBooking.airBnB.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    public LocalDateTime timeStamp;
    public HttpStatus statusCode;
    public String message;
}
