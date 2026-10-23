package com.ismarianto.microservice.dto;

import org.springframework.http.HttpStatus;

public record GlobalResponse<T>(String code, String messages, T data, HttpStatus status) {

}
