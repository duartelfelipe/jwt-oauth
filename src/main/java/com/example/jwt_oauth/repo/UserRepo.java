package com.example.jwt_oauth.repo;

import com.example.jwt_oauth.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepo extends ListCrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
