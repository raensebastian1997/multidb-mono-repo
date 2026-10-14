package com.ismarianto.microservice.services;

import com.ismarianto.microservice.models.primary.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersServices {
    Users createUser(Users users);
    Users UpdateUser(Users users);
    List<Users> getAllUsers();
    Users getUserById(Long id);

}
