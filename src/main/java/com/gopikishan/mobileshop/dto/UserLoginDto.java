package com.gopikishan.mobileshop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginDto {

    @NotBlank(message = "Email is required!")
    @Email(message = "Invalid email format!")
    @Schema(description = "The user's email")
    private String email;

    @NotBlank(message = "Password is required!")
    @Schema(description = "The user's password")
    private String password;

}
