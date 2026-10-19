package com.example.AuthService;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Requestdto {
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userRole;
}
