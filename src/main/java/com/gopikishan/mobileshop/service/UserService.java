package com.gopikishan.mobileshop.service;

import com.gopikishan.mobileshop.dto.UserLoginDto;
import com.gopikishan.mobileshop.dto.UserRegisterDto;
import com.gopikishan.mobileshop.dto.UserResponseDto;
import com.gopikishan.mobileshop.entity.User;
import com.gopikishan.mobileshop.exception.DuplicateEmailException;
import com.gopikishan.mobileshop.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JWTService jwtService;

    public UserService(UserRepository repository, AuthenticationManager authenticationManager, PasswordEncoder encoder, JWTService jwtService) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }


    public UserResponseDto signup(UserRegisterDto userRegisterDto) {

        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())){
            throw new RuntimeException("Password and Confirm Password do not match!");
        }

        if (repository.existsByEmail(userRegisterDto.getEmail().trim().toLowerCase())) {
            throw new  DuplicateEmailException("User Already exist's with email: " + userRegisterDto.getEmail().trim().toLowerCase());
        }

        User user = mapToEntity(userRegisterDto);
        user.setRole("USER");
        User userResponse = repository.save(user);

        return mapToResponseDto(userResponse);
    }

    private UserResponseDto mapToResponseDto(User userResponse) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userResponse.getId());
        userResponseDto.setName(userResponse.getName());
        userResponseDto.setEmail(userResponse.getEmail().trim().toLowerCase());

        return userResponseDto;
    }

    private User mapToEntity(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setName(userRegisterDto.getName());
        user.setEmail(userRegisterDto.getEmail().trim().toLowerCase());
        user.setPassword(encoder.encode(userRegisterDto.getPassword()));
        return user;
    }


    public Map<String, String> verify(UserLoginDto userLoginDto) {
        String email = userLoginDto.getEmail().trim().toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, userLoginDto.getPassword())
                );

        if (authentication.isAuthenticated()) {

            String token = jwtService.generateToken(email);

            String role = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .orElseThrow()
                    .getAuthority();

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);

            return response;
        }

        throw new BadCredentialsException("Login failed!");
    }

}
