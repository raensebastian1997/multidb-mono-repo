package com.ismarianto.microservice.repository.primary;

import com.ismarianto.microservice.models.primary.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("primaryUsersRepository")
public interface UsersRepository extends JpaRepository<Users, Long> {

}