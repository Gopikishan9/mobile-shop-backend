package com.gopikishan.mobileshop.controller;

import com.gopikishan.mobileshop.dto.UserLoginDto;
import com.gopikishan.mobileshop.dto.UserRegisterDto;
import com.gopikishan.mobileshop.dto.UserResponseDto;
import com.gopikishan.mobileshop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User APIs", description = "User login and register")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Signup")
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserRegisterDto userRegisterDto){

        UserResponseDto userResponseDto = service.signup(userRegisterDto);

        return ResponseEntity.ok(userResponseDto);
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserLoginDto userLoginDto){
        Map<String, String> result =  service.verify(userLoginDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }


}
