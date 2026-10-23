package com.ismarianto.microservice.services;

import com.ismarianto.microservice.dto.GlobalResponse;
import com.ismarianto.microservice.models.primary.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;


public interface UsersServices extends UserDetailsService {

    Users createUser(Users users);

    Users UpdateUser(Users users);

    List<Users> getAllUsers();

    Users getUserById(Long id);

    GlobalResponse<Object> loginAction(String usernane, String passowrd);
}
