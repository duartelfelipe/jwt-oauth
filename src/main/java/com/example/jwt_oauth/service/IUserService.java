package com.example.jwt_oauth.service;

import com.example.jwt_oauth.model.User;

public interface IUserService {
    User addUser(User user);
    User getByUsername(String username);
}
