package com.netology.diploma.loikokate.diplomabackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logout")
@Slf4j
public class LogoutController {

    @PostMapping
    public void logout() {
        log.debug("Logout");
    }
}
