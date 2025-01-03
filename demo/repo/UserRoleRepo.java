package com.Hashing.demo.repo;

import com.Hashing.demo.model.UserRole;
import com.Hashing.demo.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepo extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(Users user);
}