package com.Hashing.demo.service;

import com.Hashing.demo.model.LoginResponse;
import com.Hashing.demo.model.UserRole;
import com.Hashing.demo.model.Users;
import com.Hashing.demo.repo.UserRepo;
import com.Hashing.demo.repo.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users register(Users user, String role) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Users savedUser = repo.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRoleRepo.save(userRole);

        return savedUser;
    }

    public LoginResponse login(String useremail, String rawPassword) {
        Users user = repo.findByUseremail(useremail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Fetch the user's role
        String role = userRoleRepo.findByUser(user).stream()
                .map(UserRole::getRole)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User has no role assigned"));

        // Generate the JWT token
        String jwtToken = jwtService.generateToken(user);

        // Return both the token and the role
        return new LoginResponse(jwtToken, role);
    }


    public LoginResponse googleLogin(OidcUser oidcUser) {
        String email = oidcUser.getEmail();

        // Find the user by email or create a new user
        Users user = repo.findByUseremail(email).orElseGet(() -> {
            Users newUser = new Users();
            newUser.setUseremail(email);
            return repo.save(newUser);
        });

        // Fetch the user's role (assuming you are storing roles in the UserRole entity)
        String role = userRoleRepo.findByUser(user).stream()
                .map(UserRole::getRole)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User has no role assigned"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        // Return both the token and role in the LoginResponse
        return new LoginResponse(jwtToken, role);
    }


    public List<String> getCandidateEmails() {
        List<Users> candidates = repo.findByRoleIgnoreCase("Candidate");

        // Extracting email addresses and returning them as a list
        return candidates.stream()
                .map(Users::getUseremail)
                .collect(Collectors.toList());
    }
}
