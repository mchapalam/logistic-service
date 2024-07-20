package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM app_user u WHERE u.username = :username")
    User findByUsername(String username);
}
