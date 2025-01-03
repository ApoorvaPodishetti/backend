package com.Hashing.demo.controller;

import com.Hashing.demo.model.LoginResponse;
import com.Hashing.demo.model.UserRegistrationRequest;
import com.Hashing.demo.model.Users;
import com.Hashing.demo.repo.UserRepo;
import com.Hashing.demo.service.JWTService;
import com.Hashing.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    // User Registration Endpoint
    @PostMapping("/signup")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserRegistrationRequest registrationRequest) {
        Users user = registrationRequest.getUser();
        String role = registrationRequest.getRole();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "User data is missing"));
        }
        System.out.println("Received user data: " + user);
        System.out.println("Received role: " + role);

        Users registeredUser = service.register(user, role);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully registered");

        return ResponseEntity.ok(response);
    }

    // User Login Endpoint - Return JWT Token and Role
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<LoginResponse> login(@RequestBody Users user) {
        try {
            // Get JWT Token and Role
            LoginResponse loginResponse = service.login(user.getUseremail(), user.getPassword());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Google Login Endpoint
    @PostMapping("/google-login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<LoginResponse> googleLogin(OidcUser oidcUser) {
        try {
            // Get both JWT Token and Role from the service
            LoginResponse loginResponse = service.googleLogin(oidcUser);

            // Return the response with both JWT Token and Role
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    // Get list of candidate emails



    @PostMapping("/api/logout")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<?> logout() {
        // If you're using JWT, you don't actually need to do anything server-side
        // because the client will stop sending the JWT after logout.
        return ResponseEntity.ok().build(); // Simply return an empty response
    }
}
