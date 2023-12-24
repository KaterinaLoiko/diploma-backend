package com.netology.diploma.loikokate.diplomabackend.service.impl;

import com.netology.diploma.loikokate.diplomabackend.dao.UserEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginRequest;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginResponse;
import com.netology.diploma.loikokate.diplomabackend.exception.UserNotFoundException;
import com.netology.diploma.loikokate.diplomabackend.repository.UserRepository;
import com.netology.diploma.loikokate.diplomabackend.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    UserRepository userRepository;

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.debug(String.format("Authentication with login %s and password %s",
                loginRequest.getLogin(), loginRequest.getPassword()));
        UserEntity user = userRepository.findByLoginAndPassword(loginRequest.getLogin(), loginRequest.getPassword());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return LoginResponse.builder().authToken(user.getAuthToken()).build();
    }

    @Override
    public UserEntity isTokenValid(String token) {
        log.debug("Get user from db with token " + token);
        return userRepository.findByAuthToken(token);
    }
}
