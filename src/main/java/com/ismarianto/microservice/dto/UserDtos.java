package com.ismarianto.microservice.dto;

import java.util.List;

public class UserDtos {


    public record UserDTO(
            Long id,
            String name,
            String email,
            String password
    ) {
    }

    public record UserListApiResponse(
            List<UserDTO> data,
            String message
    ) {
    }
}