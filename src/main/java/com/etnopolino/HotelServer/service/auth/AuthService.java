package com.etnopolino.HotelServer.service.auth;

import com.etnopolino.HotelServer.dto.SignupRequest;
import com.etnopolino.HotelServer.dto.UserDto;

public interface AuthService {

    void signUpUser(SignupRequest signupRequest);
    boolean hasEmailWithEmail(String email);
    void VerifyAccount(String token);
}
