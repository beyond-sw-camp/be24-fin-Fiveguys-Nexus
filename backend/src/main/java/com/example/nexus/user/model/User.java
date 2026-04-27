package com.example.nexus.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "User")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    private String email;
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(nullable = false)
    private String tell;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
