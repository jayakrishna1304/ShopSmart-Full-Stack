package com.example.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody Requestdto request) {
        System.out.println(request);
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestdto login) {
        return authService.Login(login);
    }
}