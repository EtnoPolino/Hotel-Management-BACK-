package com.etnopolino.HotelServer.securityUtils;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import java.time.Instant;


@Component
@RequiredArgsConstructor
@Data
public class JwtProvider{

    private final JwtEncoder jwtEncoder;

    private String generateTokenWithUsername(String username){
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .subject(username)
                                          .issuedAt(Instant.now())
                                          .expiresAt(Instant.now().plusMillis(1000 * 60 * 60 * 24))
                                          .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(); //on extrait tous les claims du token
    }

    public String generateToken(Authentication authentication){
        User principal = (User) authentication.getPrincipal();
        return generateTokenWithUsername(principal.getUsername());
    }

}
