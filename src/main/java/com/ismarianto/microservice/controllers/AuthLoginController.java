package com.ismarianto.microservice.controllers;

import com.ismarianto.microservice.dto.GlobalResponse;
import com.ismarianto.microservice.models.primary.Users;
import com.ismarianto.microservice.security.JwtToken;
import com.ismarianto.microservice.services.UsersServices;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthLoginController {

    private final UsersServices usersServices;

    public AuthLoginController(UsersServices usersServices) {
        this.usersServices = usersServices;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Users loginUser) {
        GlobalResponse<Object> resp = usersServices.loginAction(loginUser.getUsername(), loginUser.getPassword());
        try {
            return new ResponseEntity<>(resp, resp.status());
        } catch (Exception e) {
            return new ResponseEntity<>(resp, resp.status());
        }
    }

    public record LoginUser(String username, String password, String token, String refreshToken
    ) {
    }


}
