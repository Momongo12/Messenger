package com.example.messenger.repository;


import com.example.messenger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE unique_username LIKE :usernamePrefix%", nativeQuery = true)
    List<User> findByUniqueUsernameStartingWith(@Param("usernamePrefix") String usernamePrefix);

    User findByUniqueUsername(String uniqueUsername);
}
