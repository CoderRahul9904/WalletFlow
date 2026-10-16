package com.rsm.walletflow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String phone;

    @OneToOne(mappedBy = "user",
            fetch = FetchType.LAZY)
    private Wallet wallet;

    private LocalDateTime createdAt;
}
