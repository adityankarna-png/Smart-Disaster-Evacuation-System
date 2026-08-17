package com.disaster.service;

import com.disaster.model.User;

public interface AuthenticationService {

    User login(String username, String password);
}