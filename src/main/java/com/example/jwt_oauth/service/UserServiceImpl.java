package com.example.jwt_oauth.service;

import com.example.jwt_oauth.config.security.TokenUtil;
import com.example.jwt_oauth.dto.TokenDTO;
import com.example.jwt_oauth.model.User;
import com.example.jwt_oauth.repo.UserRepo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepo repo;

    public UserServiceImpl(UserRepo repo) {
        this.repo = repo;
    }

    @Override
    public User addUser(User user) {
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(
                    encoder.encode(user.getPassword())
            );
            return repo.save(user);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error.", ex);
        }
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public TokenDTO login(User user) {
        User baseUser = repo.findByUsername(user.getUsername()).orElseThrow(
                () -> new NoSuchElementException("User not found:" + user.getUsername())
        );

        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        if (encode.matches(user.getPassword(), baseUser.getPassword())) {
            return TokenUtil.encode(user);
        }

        throw new BadCredentialsException("Unauthorized user: " + user.getUsername());
    }
}
