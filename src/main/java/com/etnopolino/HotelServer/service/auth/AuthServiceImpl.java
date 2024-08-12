package com.etnopolino.HotelServer.service.auth;

import com.etnopolino.HotelServer.dto.AuthenticateResponse;
import com.etnopolino.HotelServer.dto.LoginRequest;
import com.etnopolino.HotelServer.dto.SignupRequest;
import com.etnopolino.HotelServer.entity.NotificationEmail;
import com.etnopolino.HotelServer.entity.User;
import com.etnopolino.HotelServer.entity.VerificationToken;
import com.etnopolino.HotelServer.enums.UserRole;
import com.etnopolino.HotelServer.exceptions.SpringHotelException;
import com.etnopolino.HotelServer.repositories.UserRepository;
import com.etnopolino.HotelServer.repositories.VerificationTokenRepository;
import com.etnopolino.HotelServer.securityUtils.JwtProvider;
import com.etnopolino.HotelServer.service.mailing.MailService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional // forced to used transactional or else we cant enable our customer when the email is sent by email
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostConstruct
    public void createAdminAccount(){
        Optional<User> optionalAdminUser = userRepository.findByUserRole(UserRole.ADMIN);

        if(optionalAdminUser.isEmpty()){
            User user = new User();
            user.setEmail("admin@test.com");
            user.setName("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setUserRole(UserRole.ADMIN);
            user.setCreated(Instant.now());
            user.setEnabled(true);
            userRepository.save(user);

            System.out.println("ADMIN account created successfully");
        }else{
            System.out.println("ADMIN account already exist");
        }

    }

    @Override
    @Transactional //interacting with relational database
    public void signUpUser(SignupRequest signupRequest) {

        if(userRepository.findFirstByEmail(signupRequest.getEmail()).isPresent()){
            throw new EntityExistsException("User already present with email "+signupRequest.getEmail());
        }

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserRole(UserRole.CUSTOMER);
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        String token = generateVerifyToken(user);

        mailService.sendMail(new NotificationEmail("Please Activate your Account", user.getEmail(), "Thank you for signing up to our Hotel, " +
                                                                                                    "Please click on the below url to activate your account : " +
                                                                                                    "http://localhost:8080/api/auth/accountVerification/"+token));

    }
    @Override
    public void VerifyAccount(String token){
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow( () -> new SpringHotelException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Override
    public boolean hasEmailWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @Override
    public AuthenticateResponse login(LoginRequest loginRequest){

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate); //si on veut savoir si un user est lohin ou non, on rgarde juste le securityContext pour l'authentification
        String token = jwtProvider.generateToken(authenticate);
        Optional<User> optionalUser = userRepository.findFirstByEmail(loginRequest.getEmail());

        if(optionalUser.isPresent() && optionalUser.get().isEnabled()){
            return AuthenticateResponse.builder()
                    .authenticateToken(token)
                    .userId(optionalUser.get().getId())
                    .username(optionalUser.get().getUsername())
                    .expiresAt(Instant.now().plusMillis(200000))
                    .build();
        }

        throw new SpringHotelException("User either doesnt exist or isnt activated");
    }



//-------------- private method ----------------------------------------------------------

    private String generateVerifyToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(); //we define a new verification token for each user created
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String emailUser = verificationToken.getUser().getUsername();  //username here is the email..
        User user = userRepository.findFirstByEmail(emailUser)
                                  .orElseThrow( ()-> new SpringHotelException("User not found with email of "+emailUser));

        user.setEnabled(true);
        userRepository.save(user);
    }



}
