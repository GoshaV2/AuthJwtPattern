package com.t1.authjwtpattern.repository;

import com.t1.authjwtpattern.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);
}
