package com.rsm.walletflow.repository;

import com.rsm.walletflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserById(String id);
    Optional<User> getUserByEmail(String email);
}
