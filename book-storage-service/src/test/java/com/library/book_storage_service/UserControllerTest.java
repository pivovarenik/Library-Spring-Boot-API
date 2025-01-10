package com.library.book_storage_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.book_storage_service.controllers.UserController;
import com.library.book_storage_service.models.TokenReqRes;
import com.library.book_storage_service.models.User;
import com.library.book_storage_service.repos.UserRepository;
import com.library.book_storage_service.services.JwtTokenApi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenApi jwtTokenApi;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerUser_ShouldReturnCreated_WhenUserIsRegisteredSuccessfully() throws Exception {
        User user = new User(1L, "testUser", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void registerUser_ShouldReturnInternalServerError_WhenSaveFails() throws Exception {
        User user = new User(1L, "testUser", "password123");

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Save failed"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal Server Error While Saving User"));
    }

    @Test
    void generateToken_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        User user = new User(1L, "testUser", "hashedPassword");
        TokenReqRes tokenReqRes = new TokenReqRes("testUser", "password123", null, null);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtTokenApi.generateToken("testUser")).thenReturn("testToken");

        mockMvc.perform(post("/generate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenReqRes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.token").value("testToken"))
                .andExpect(jsonPath("$.expirationTime").value("1 hour"));
    }

    @Test
    void generateToken_ShouldReturnUnauthorized_WhenPasswordIsInvalid() throws Exception {
        User user = new User(1L, "testUser", "hashedPassword");
        TokenReqRes tokenReqRes = new TokenReqRes("testUser", "wrongPassword", null, null);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/generate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenReqRes)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void generateToken_ShouldReturnBadRequest_WhenUserDoesNotExist() throws Exception {
        TokenReqRes tokenReqRes = new TokenReqRes("nonexistentUser", "password123", null, null);

        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        mockMvc.perform(post("/generate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenReqRes)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User does not exist"));
    }

    @Test
    void validateToken_ShouldReturnValidationResult() throws Exception {
        TokenReqRes tokenReqRes = new TokenReqRes(null, null, "validToken", null);

        when(jwtTokenApi.validateToken("validToken")).thenReturn("Token is valid");

        mockMvc.perform(post("/validate-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenReqRes)))
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }
}
