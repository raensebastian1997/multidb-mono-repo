package com.ismarianto.microservice.models.primary;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String username;
    private String fullName;
    private String roles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;




    enum Roles {
        ADMIN("Admin"),
        USER("user"),
        MANAGER("manager"),
        GUEST("guest");

        private final String label;

        // Constructor
        Roles(String label) {
            this.label = label;
        }

        // Getter method
        public String getLabel() {
            return this.label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }







}

