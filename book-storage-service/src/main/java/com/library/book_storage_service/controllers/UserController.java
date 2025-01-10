package com.library.book_storage_service.controllers;


import com.library.book_storage_service.config.PasswordEncoder;
import com.library.book_storage_service.models.TokenReqRes;
import com.library.book_storage_service.models.User;
import com.library.book_storage_service.repos.UserRepository;
import com.library.book_storage_service.services.JwtTokenApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {

    private final JwtTokenApi jwtTokenApi;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(JwtTokenApi jwtTokenApi, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtTokenApi = jwtTokenApi;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        try{
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error While Saving User");
        }
    }

    @PostMapping("/generate-token")
    public ResponseEntity<Object> generateToken(@RequestBody TokenReqRes tokenReqRes) {
        User savedUser = userRepository.findByUsername(tokenReqRes.getUsername());
        if(savedUser != null){
            if(passwordEncoder.matches(tokenReqRes.getPassword(), savedUser.getPassword())){
                String token = jwtTokenApi.generateToken(tokenReqRes.getUsername());
                tokenReqRes.setToken(token);
                tokenReqRes.setExpirationTime("1 hour");
                return ResponseEntity.ok(tokenReqRes);
            }
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist");
    }


    @PostMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestBody TokenReqRes tokenReqRes) {
       return ResponseEntity.ok(jwtTokenApi.validateToken(tokenReqRes.getToken()));
    }

}
