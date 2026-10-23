package com.ismarianto.microservice.controllers;


import com.ismarianto.microservice.dto.UserDtos.UserDTO;
import com.ismarianto.microservice.dto.UserDtos.UserListApiResponse;

import com.ismarianto.microservice.lib.RestConsume;
import com.ismarianto.microservice.models.primary.Users;
import com.ismarianto.microservice.repository.primary.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserControllers {

    private final UsersRepository usersRepository;
    private final JdbcTemplate warehouseJdbcTemplate;


    public UserControllers(UsersRepository usersRepository, @Qualifier("warehouseJdbcTemplate") JdbcTemplate warehouseJdbcTemplate) {
        this.usersRepository = usersRepository;
        this.warehouseJdbcTemplate = warehouseJdbcTemplate;
    }


    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<Users> users = usersRepository.findAll().stream().peek(e-> e.setPassword(null)).toList();
            return new ResponseEntity<>(Map.of("message", "success", "data", users), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody Users users) {
        try {
            Users user = usersRepository.save(users);
            return new ResponseEntity<>(Map.of("message", "success", "data", user), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional("primaryTransactionManager")
    @PutMapping
    public ResponseEntity<Object> updateUser(@RequestBody Users users) {
        try {
            Users user = usersRepository.save(users);
            return new ResponseEntity<>(Map.of("message", "success", "data", user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        try {
            Users user = usersRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
            return new ResponseEntity<>(Map.of("message", "success", "data", user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional("primaryTransactionManager")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        try {
            usersRepository.deleteById(id);
            return new ResponseEntity<>(Map.of("message", "success", "data", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/departement")
    public ResponseEntity<Object> getDepartement() {
        try {

            List<Map<String, Object>> departments = warehouseJdbcTemplate.queryForList("SELECT * FROM departemen");


            UserListApiResponse response = RestConsume.call(
                    "http://localhost:8080/api/v1/users",
                    "GET",
                    UserListApiResponse.class
            );

            List<UserDTO> users = response.data();
            return new ResponseEntity<>(
                    Map.of(
                            "message", "success",
                            "data", departments,
                            "users", users
                    ),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getDepartement: {}", e.getMessage());
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();

            return new ResponseEntity<>(
                    Map.of(
                            "message", e.getMessage(),
                            "rootCause", root.getMessage()
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}