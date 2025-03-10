package com.library.book_storage_service.services;


import com.library.book_storage_service.models.User;
import com.library.book_storage_service.repos.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User verify(User input) throws NoSuchElementException {
        Authentication auth =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
        if (auth.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            String username = userDetails.getUsername();
            Optional<User> userFromDb = userRepository.findByUsername(username);
            return userFromDb.get();
        } else {
            throw new NoSuchElementException();
        }
    }
}
