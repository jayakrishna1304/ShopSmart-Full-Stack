package com.example.AuthService;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class Retailerdto {
    private Integer ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPassword;
//    private Date createdDate;
}
