package com.library.book_storage_service.controllers;

import com.library.book_storage_service.mappers.UserMapper;
import com.library.book_storage_service.models.User;
import com.library.book_storage_service.services.JwtCore;
import com.library.book_storage_service.services.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtCore jwtCore;
    private final UserMapper userMapper;
    public UserController(UserService userService,JwtCore jwtCore, UserMapper userMapper) {
        this.userService = userService;
        this.jwtCore = jwtCore;
        this.userMapper = userMapper;
    }
    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody User input) {
        User user = userService.registerUser(input);
        return new ResponseEntity<>(userMapper.userToUserDTO(user), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody User input) {
        try{
            User user = userService.verify(input);
            return new ResponseEntity<>(jwtCore.generateToken(user), HttpStatus.OK);
        }
        catch(NoSuchElementException e){
            return new ResponseEntity<>("Couldn't verify. Try again", HttpStatus.UNAUTHORIZED);
        }
    }
}