package com.disaster.service;

import com.disaster.dao.UserDAO;
import com.disaster.model.User;

public class AuthenticationServiceImpl
        implements AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationServiceImpl() {

        userDAO = new UserDAO();
    }

    @Override
    public User login(
            String username,
            String password
    ) {

        if (username == null ||
                username.trim().isEmpty()) {

            return null;
        }

        if (password == null ||
                password.isEmpty()) {

            return null;
        }

        return userDAO.authenticate(
                username.trim(),
                password
        );
    }
}