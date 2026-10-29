package com.gopikishan.mobileshop.service;

import com.gopikishan.mobileshop.dto.UserLoginDto;
import com.gopikishan.mobileshop.dto.UserRegisterDto;
import com.gopikishan.mobileshop.dto.UserResponseDto;
import com.gopikishan.mobileshop.entity.User;
import com.gopikishan.mobileshop.exception.DuplicateEmailException;
import com.gopikishan.mobileshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void signupShouldSaveUserAndReturnResponseDto() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Gopi");
        dto.setEmail("GOPI@EXAMPLE.COM");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Gopi");
        savedUser.setEmail("gopi@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");

        when(repository.existsByEmail("gopi@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.signup(dto);

        assertEquals(1L, response.getId());
        assertEquals("Gopi", response.getName());
        assertEquals("gopi@example.com", response.getEmail());

        verify(repository).existsByEmail("gopi@example.com");
        verify(encoder).encode("password123");
        verify(repository).save(any(User.class));
    }

    @Test
    void signupShouldThrowExceptionWhenPasswordsDoNotMatch() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Gopi");
        dto.setEmail("gopi@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("different123");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.signup(dto)
        );

        assertEquals(
                "Password and Confirm Password do not match!",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }

    @Test
    void signupShouldThrowExceptionWhenEmailAlreadyExists() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Gopi");
        dto.setEmail("GOPI@EXAMPLE.COM");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        when(repository.existsByEmail("gopi@example.com"))
                .thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> userService.signup(dto)
        );

        assertEquals(
                "User Already exist's with email: gopi@example.com",
                exception.getMessage()
        );

        verify(repository).existsByEmail("gopi@example.com");
        verify(repository, never()).save(any(User.class));
        verifyNoInteractions(encoder);
    }

    @Test
    void verifyShouldReturnTokenAndRoleWhenCredentialsAreValid() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("GOPI@EXAMPLE.COM");
        dto.setPassword("password123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        doReturn(
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ).when(authentication).getAuthorities();

        when(jwtService.generateToken("gopi@example.com"))
                .thenReturn("mock-jwt-token");

        Map<String, String> response = userService.verify(dto);

        assertEquals("mock-jwt-token", response.get("token"));
        assertEquals("ROLE_USER", response.get("role"));

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(jwtService).generateToken("gopi@example.com");
    }

    @Test
    void verifyShouldThrowBadCredentialsWhenAuthenticationFails() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("gopi@example.com");
        dto.setPassword("wrongPassword");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> userService.verify(dto)
        );

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verifyNoInteractions(jwtService);
    }

    @Test
    void verifyShouldNormalizeEmailBeforeAuthentication() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("  GOPI@EXAMPLE.COM  ");
        dto.setPassword("password123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.isAuthenticated()).thenReturn(true);

        doReturn(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        when(jwtService.generateToken("gopi@example.com"))
                .thenReturn("mock-jwt-token");

        Map<String, String> response = userService.verify(dto);

        assertEquals("mock-jwt-token", response.get("token"));
        assertEquals("ROLE_USER", response.get("role"));

        verify(jwtService).generateToken("gopi@example.com");
    }

    @Test
    void signupShouldNormalizeEmailAndAssignUserRole() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Gopi");
        dto.setEmail("GOPI@EXAMPLE.COM");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        when(repository.existsByEmail("gopi@example.com"))
                .thenReturn(false);
        when(encoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserResponseDto response = userService.signup(dto);

        assertEquals("gopi@example.com", response.getEmail());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("gopi@example.com", savedUser.getEmail());
        assertEquals("USER", savedUser.getRole());
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void verifyShouldPropagateAuthenticationException() {
        UserLoginDto dto = new UserLoginDto();
        dto.setEmail("gopi@example.com");
        dto.setPassword("wrongPassword");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.verify(dto)
        );

        assertEquals("Bad credentials", exception.getMessage());

        verifyNoInteractions(jwtService);
    }


}