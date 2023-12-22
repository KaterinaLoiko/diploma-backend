package com.netology.diploma.loikokate.diplomabackend.service;

import com.netology.diploma.loikokate.diplomabackend.dao.UserEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginRequest;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginResponse;

public interface LoginService {

    LoginResponse authenticate(LoginRequest loginRequest);

    UserEntity isTokenValid(String token);
}
