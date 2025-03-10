package com.library.book_storage_service.controllers;

import com.library.book_storage_service.mappers.UserMapper;
import com.library.book_storage_service.models.User;
import com.library.book_storage_service.services.UserService;
import com.library.book_storage_service.services.JwtCore;
import com.library.book_storage_service.models.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtCore jwtCore;

    private User user;
    @MockitoBean
    private UserMapper userMapper;
    @BeforeEach
    void setUp() {
        user = new User(null, "testUser", "password123", Role.Librarian);
    }

    @Test
    @WithMockUser
    void testSignup_Success() throws Exception {
        User userWithId = new User(5L, "testUser", "password123", Role.Librarian);
        Mockito.when(userService.registerUser(Mockito.any(User.class))).thenReturn(userWithId);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content("{\"username\":\"testUser\",\"password\":\"password123\",\"role\":\"Librarian\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testSignup_Failure_DuplicateUsername() throws Exception {
        Mockito.when(userService.registerUser(Mockito.any(User.class))).thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content("{\"username\":\"testUser\",\"password\":\"password123\",\"role\":\"Librarian\"}"))
                .andExpect(status().is5xxServerError());
    }
    @Test
    @WithMockUser
    void testSignin_Success() throws Exception {
        String jwtToken = "fake-jwt-token";
        Mockito.when(userService.verify(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(jwtCore.generateToken(Mockito.any(User.class))).thenReturn(jwtToken);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content("{\"username\":\"testUser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(jwtToken));
    }

    @Test
    @WithMockUser
    void testSignin_Failure_InvalidCredentials() throws Exception {
        Mockito.when(userService.verify(Mockito.any(User.class)))
                .thenThrow(new NoSuchElementException("Invalid credentials"));

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content("{\"username\":\"testUser\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Couldn't verify. Try again"));  // Expecting the custom error message
    }
}
