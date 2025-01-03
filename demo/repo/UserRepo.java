package com.Hashing.demo.repo;

import com.Hashing.demo.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUseremail(String useremail);
    @Query("SELECT u FROM Users u JOIN u.userRoles ur WHERE LOWER(ur.role) = LOWER(:role)")
    List<Users> findByRoleIgnoreCase(String role);
}
