package com.gopikishan.mobileshop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegisterDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters")
    private String name;

    @Email(message = "Invalid email!")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, max = 20, message = "password must be minimum 8 characters!")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character (@#$%^&+=)"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

}
