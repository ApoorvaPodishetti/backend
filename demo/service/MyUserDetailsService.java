package com.Hashing.demo.service;

import com.Hashing.demo.model.UserPrincipal;
import com.Hashing.demo.model.Users;
import com.Hashing.demo.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = repo.findByUseremail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(user.get().getUseremail(), user.get().getPassword(), new ArrayList<>());
    }

}
