package com.Hashing.demo.config;

import com.Hashing.demo.service.JWTService;
import com.Hashing.demo.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " from the token string
            email = jwtService.extractUserName(token); // Make sure this method extracts email from JWT
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Use the email to load user details instead of username
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(email); // Ensure this loads by email
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            OidcUser oidcUser = new DefaultOidcUser(null, null);  // Replace nulls with actual values as per your setup
            OAuth2AuthenticationToken oauthToken = new OAuth2AuthenticationToken(oidcUser, null, "google");
            SecurityContextHolder.getContext().setAuthentication(oauthToken);
        }

        filterChain.doFilter(request, response);
    }
}
