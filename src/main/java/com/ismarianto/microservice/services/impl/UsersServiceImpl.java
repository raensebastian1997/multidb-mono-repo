package com.ismarianto.microservice.services.impl;

import com.ismarianto.microservice.dto.GlobalResponse;
import com.ismarianto.microservice.models.primary.Users;
import com.ismarianto.microservice.repository.primary.UsersRepository;
import com.ismarianto.microservice.security.JwtToken;
import com.ismarianto.microservice.services.UsersServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class UsersServiceImpl implements UsersServices {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Users createUser(Users users) {
        String password = new BCryptPasswordEncoder(14).encode(users.getPassword());
        users.setPassword(password);
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


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + username));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())  // HARUS hash BCrypt di DB
//                .roles(user.getRoles().toString())
                .authorities(Collections.emptyList())
                .build();
    }

    @Override
    public GlobalResponse<Object> loginAction(String username, String passowrd) {
        try {
            Optional<Users> resp = usersRepository.findByUsername(username);
            log.info("validation if rules nt noul {} ", resp);
            if (resp.isEmpty()) {
                return new GlobalResponse<Object>("400", "Username dan password salah", null, HttpStatus.BAD_REQUEST);
            }
            log.info("get result paramter login {} ", resp.get().getEmail());
            try {

                Users user = resp.get();
                if (!passwordEncoder.matches(passowrd, user.getPassword())) {
                    return new GlobalResponse<Object>("400", "Username dan password salah", null, HttpStatus.BAD_REQUEST);
                }
                UserDetails userDetails = User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRoles() != null ? user.getRoles() : "USER"))) // Set roles/permissions
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build();


                Map<String, Object> claims = Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "userId", user.getId(),
                        "roles", user.getRoles() != null ? user.getRoles() : "USER"
                );
                String token = jwtToken.buildToken(claims, userDetails, 3600); // 3600 = 1 jam
                return new GlobalResponse<Object>("200", "Login Berhasil",
                        Map.of(
                                "usernames", user.getUsername(),
                                "email", user.getEmail(),
                                "tokenss", token
                        ),
                        HttpStatus.OK
                );
            } catch (BadCredentialsException e) {
                log.warn("BadCredentials: {}", e.getMessage());
                log.error("Error handling login: {}", e.getMessage(), e);
                return new GlobalResponse<Object>("400", e.getMessage(),
                        null, HttpStatus.BAD_REQUEST
                );
            }
        } catch (Exception e) {
            log.error("Error handling login: {}", e.getMessage(), e);
            return new GlobalResponse<Object>("400", e.getMessage(),
                    null, HttpStatus.BAD_REQUEST
            );
        }
    }

    public static  void main(String[] args) {
        String password = new BCryptPasswordEncoder(14).encode("ismarianto");
        System.out.println("=== Password fix ==>"+password);
    }
}
