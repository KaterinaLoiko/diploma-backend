package com.netology.diploma.loikokate.diplomabackend.filter;

import com.netology.diploma.loikokate.diplomabackend.dao.UserEntity;
import com.netology.diploma.loikokate.diplomabackend.exception.UserNotFoundException;
import com.netology.diploma.loikokate.diplomabackend.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("Authentication");
        if (request.getRequestURI().equals("/login")) {
            log.debug("Login request");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Get cookies");
        Cookie cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("auth-token")).findAny().orElse(null);
        if (cookie == null) {
            throw new UserNotFoundException("No token");
        }

        log.debug("Get user");
        UserEntity user = loginService.isTokenValid(cookie.getValue());
        if (user == null) {
            throw new UserNotFoundException("Invalid token");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
                new ArrayList<>());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
