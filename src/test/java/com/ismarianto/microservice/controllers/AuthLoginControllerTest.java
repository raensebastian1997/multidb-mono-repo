package com.ismarianto.microservice.controllers;

import com.ismarianto.microservice.models.primary.Users;
import com.ismarianto.microservice.repository.primary.UsersRepository;
import com.ismarianto.microservice.security.JwtToken;
import com.ismarianto.microservice.services.UsersServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthLoginControllerTest {

    private MockMvc mockMvc;

    private AuthenticationManager authenticationManager;

    private UsersRepository usersRepository;

    private JwtToken jwtToken;

    private UsersServices usersServices;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        usersRepository = mock(UsersRepository.class);
        jwtToken = mock(JwtToken.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new AuthLoginController(usersServices)
        ).build();
    }

    @Test
    void loginDenganKredensialValidMengembalikanToken() throws Exception {
        Users user = createUser();
        UserDetails userDetails = User.withUsername("rian")
                .password("encoded-password")
                .authorities("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        when(usersRepository.findByUsername("rian")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtToken.buildToken(anyMap(), eq(userDetails), eq(3600L)))
                .thenReturn("test-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "rian",
                                  "password": "correct-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernames").value("rian"))
                .andExpect(jsonPath("$.email").value("rian@example.com"))
                .andExpect(jsonPath("$.tokenss").value("test-jwt-token"));
    }

    @Test
    void loginDenganUsernameYangTidakDitemukanMengembalikanBadRequest() throws Exception {
        when(usersRepository.findByUsername("tidak-ada")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "tidak-ada",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").value("username dan passwird salah"));
    }

    @Test
    void loginDenganPasswordSalahMengembalikanUnauthorized() throws Exception {
        when(usersRepository.findByUsername("rian")).thenReturn(Optional.of(createUser()));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "rian",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages").value("username atau password salah"));
    }

    private Users createUser() {
        Users user = new Users();
        user.setId(1L);
        user.setUsername("rian");
        user.setEmail("rian@example.com");
        user.setPassword("encoded-password");
        return user;
    }
}
