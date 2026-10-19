package com.example.AuthService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  AuthRepository extends JpaRepository<AuthEntity,String> {
    Optional<AuthEntity> findByUserEmail(String email);

    boolean existsByUserEmail(String email);
}
