package com.etnopolino.HotelServer.dto;

import com.etnopolino.HotelServer.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateResponse {

    private String authenticateToken;
    private Long userId;
    private String username;
    private Instant expiresAt;
}
