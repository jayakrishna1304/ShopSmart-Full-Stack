package com.example.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository db;

    @Mock
    private JwtService jwtbean;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthserviceInterface customerclient;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();

        authService.db = db;
        authService.jwtbean = jwtbean;
        authService.passwordEncoder = passwordEncoder;
        authService.customerclient = customerclient;
    }


    @Test
    void registerSuccessfully() {

        Requestdto request = new Requestdto();

        request.setUser_name("Krishna");
        request.setUser_email("krishna@gmail.com");
        request.setUser_password("password");
        request.setUser_role("customer");

        when(db.existsByUserEmail("krishna@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password"))
                .thenReturn("$2a$10$encodedPassword");

        when(db.save(any(AuthEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = authService.register(request);

        assertEquals("Registered Successfully!!", result);

        verify(db).existsByUserEmail("krishna@gmail.com");

        verify(passwordEncoder)
                .encode("password");

        verify(db)
                .save(any(AuthEntity.class));
    }


    @Test
    void registerUserAlreadyExists() {

        Requestdto request = new Requestdto();

        request.setUser_name("Krishna");
        request.setUser_email("krishna@gmail.com");
        request.setUser_password("password");
        request.setUser_role("customer");

        when(db.existsByUserEmail("krishna@gmail.com"))
                .thenReturn(true);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "User already exists!!",
                exception.getMessage()
        );

        verify(db)
                .existsByUserEmail("krishna@gmail.com");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(db, never())
                .save(any(AuthEntity.class));
    }


    @Test
    void loginSuccessfully() {

        LoginRequestdto login = new LoginRequestdto();

        login.setEmail("krishna@gmail.com");
        login.setPassword("password");

        AuthEntity user = new AuthEntity();

        user.setUserId(1);
        user.setUserName("Krishna");
        user.setUserEmail("krishna@gmail.com");
        user.setUserPassword("$2a$10$encodedPassword");
        user.setUserRole("customer");

        when(db.findByUserEmail("krishna@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "$2a$10$encodedPassword"
        )).thenReturn(true);

        when(jwtbean.generateToken(
                anyString(),
                anyString()
        )).thenReturn("jwt-token");

        String result = authService.Login(login);

        assertEquals("jwt-token", result);

        verify(db)
                .findByUserEmail("krishna@gmail.com");

        verify(passwordEncoder)
                .matches(
                        "password",
                        "$2a$10$encodedPassword"
                );
    }


    @Test
    void loginEmailNotFound() {

        LoginRequestdto login = new LoginRequestdto();

        login.setEmail("wrong@gmail.com");
        login.setPassword("password");

        when(db.findByUserEmail("wrong@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.Login(login)
                );

        assertEquals(
                "Invalid email or password!!",
                exception.getMessage()
        );

        verify(db)
                .findByUserEmail("wrong@gmail.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }


    @Test
    void loginWrongPassword() {

        LoginRequestdto login = new LoginRequestdto();

        login.setEmail("krishna@gmail.com");
        login.setPassword("wrongpassword");

        AuthEntity user = new AuthEntity();

        user.setUserId(1);
        user.setUserName("Krishna");
        user.setUserEmail("krishna@gmail.com");
        user.setUserPassword("$2a$10$encodedPassword");
        user.setUserRole("customer");

        when(db.findByUserEmail("krishna@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongpassword",
                "$2a$10$encodedPassword"
        )).thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.Login(login)
                );

        assertEquals(
                "Invalid email or password!!",
                exception.getMessage()
        );

        verify(passwordEncoder)
                .matches(
                        "wrongpassword",
                        "$2a$10$encodedPassword"
                );
    }
}