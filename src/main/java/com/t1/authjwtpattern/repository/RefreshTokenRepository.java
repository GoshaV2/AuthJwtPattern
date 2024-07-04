package com.t1.authjwtpattern.repository;

import com.t1.authjwtpattern.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    boolean existsByUserIdAndToken(UUID userId, String token);

}