package com.ismarianto.microservice.repository.primary;

import com.ismarianto.microservice.models.primary.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("primaryUsersRepository")
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsernameAndPassword(String username, String password);
    Optional<Users> findByUsername(String username);
}