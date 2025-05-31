package ru.kimvlry.kittens.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthHeadersBuilder {
    public HttpHeaders build() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getCredentials() == null) {
            log.warn("AuthHeadersBuilder: no authentication or credentials");
            return new HttpHeaders();
        }
        String token = auth.getCredentials().toString();
        log.info("Found token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

}