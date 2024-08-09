package com.etnopolino.HotelServer.dto;

import com.etnopolino.HotelServer.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
}
