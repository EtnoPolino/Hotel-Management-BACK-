package com.etnopolino.HotelServer.controller.auth;

import com.etnopolino.HotelServer.dto.AuthenticateResponse;
import com.etnopolino.HotelServer.dto.LoginRequest;
import com.etnopolino.HotelServer.dto.SignupRequest;
import com.etnopolino.HotelServer.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<String> signUpUser(@RequestBody SignupRequest signupRequest){
        if(authService.hasEmailWithEmail(signupRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exist with this email");
        }

        authService.signUpUser(signupRequest);
        return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.VerifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticateResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

}
