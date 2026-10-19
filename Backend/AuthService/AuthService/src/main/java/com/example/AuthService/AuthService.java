package com.example.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    AuthRepository db;

    @Autowired
    private RetailerServiceInterface retailerclient;

    @Autowired
    JwtService jwtbean;

    @Autowired
    AuthserviceInterface customerclient;

    @Autowired
    PasswordEncoder passwordEncoder; // Cleaner field-level injection to align with repositories

    @Transactional
    public String register(Requestdto request) {

        if (db.existsByUserEmail(request.getUserEmail())) {
            throw new RuntimeException("User already exists!!");
        }

        String encodedpass = passwordEncoder.encode(request.getUserPassword());

        AuthEntity auth = new AuthEntity();
        auth.setUserName(request.getUserName());
        auth.setUserEmail(request.getUserEmail());
        auth.setUserPassword(encodedpass);
        auth.setUserRole(request.getUserRole());
        auth.setUserDate(new Date());

        AuthEntity savedAuth = db.save(auth); // Keep reference to get generated primary ID safely

        // Routes data downstream matching roles cleanly
        if (savedAuth.getUserRole().equalsIgnoreCase("customer")) {
            authtocustomer(savedAuth);
        } else if (savedAuth.getUserRole().equalsIgnoreCase("retailer")) {
            authtoretailer(savedAuth);
        }

        return "Registered Successfully!!";
    }

    public String Login(LoginRequestdto login) {
        AuthEntity user = db.findByUserEmail(login.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!!"));

        if (!passwordEncoder.matches(login.getPassword(), user.getUserPassword())) {
            throw new RuntimeException("Invalid email or password!!");
        }

        return jwtbean.generateToken(user.getUserId(), user.getUserEmail(), user.getUserRole());
    }

    public void authtocustomer(AuthEntity auth) {
        Customerdto customer = new Customerdto();
        customer.setCustomerId(Integer.valueOf(auth.getUserId())); // Fixed data formatting type constraint conversion
        customer.setCustomerName(auth.getUserName());
        customer.setEmail(auth.getUserEmail());
        customer.setCreatedDate(auth.getUserDate());

        customerclient.createcustomer(customer);
    }

    public void authtoretailer(AuthEntity auth) {
        Retailerdto retailer = new Retailerdto();
        retailer.setOwnerId(Integer.valueOf(auth.getUserId())); // Standardized formatting tracks
        retailer.setOwnerName(auth.getUserName());
        retailer.setOwnerEmail(auth.getUserEmail());
        retailer.setOwnerPassword(auth.getUserPassword());
//        retailer.setCreatedDate(auth.getUserDate());

        retailerclient.createretailer(retailer);
    }
}
