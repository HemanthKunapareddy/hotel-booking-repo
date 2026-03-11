package com.hotelBooking.airBnB.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private final String token = "jwt";
    private final String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
}
