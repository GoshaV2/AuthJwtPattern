package com.t1.authjwtpattern.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "refresh_tokens")
@Entity
public class RefreshToken {
    @Id
    private UUID userId;
    @Column(nullable = false, unique = true)
    private String token;
    @OneToOne
    @PrimaryKeyJoinColumn
    private User user;
}
