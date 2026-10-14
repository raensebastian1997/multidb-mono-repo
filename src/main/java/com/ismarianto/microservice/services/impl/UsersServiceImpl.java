package com.ismarianto.microservice.services.impl;

import com.ismarianto.microservice.models.primary.Users;
import com.ismarianto.microservice.repository.primary.UsersRepository;

import java.util.List;
import java.util.Optional;

public record UsersServiceImpl(UsersRepository usersRepository) implements com.ismarianto.microservice.services.UsersServices {

    @Override
    public Users createUser(Users users) {
        return usersRepository.save(users);
    }

    @Override
    public Users UpdateUser(Users users) {
        return Optional.ofNullable(users)
                .map(Users::getId)
                .flatMap(usersRepository::findById)
                .map(user -> usersRepository.save(user))
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + users.getId()));
    }

    @Override
    public List<Users> getAllUsers() {
        return Optional.ofNullable(usersRepository.findAll())
                .filter(list -> !list.isEmpty())
                .map(List::copyOf)
                .orElseGet(List::of);
    }

    @Override
    public Users getUserById(Long id) {
        return Optional.ofNullable(id)
                .flatMap(usersRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}