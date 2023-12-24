package com.netology.diploma.loikokate.diplomabackend.controller;

import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginRequest;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginResponse;
import com.netology.diploma.loikokate.diplomabackend.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
@Slf4j
@AllArgsConstructor
public class LoginController {

    LoginService loginService;

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        log.debug("Login " + loginRequest);
        return loginService.authenticate(loginRequest);
    }
}
