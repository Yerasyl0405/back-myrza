package com.example.demo.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/current")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            userInfo.put("username", userDetails.getUsername());
            userInfo.put("authenticated", true);

            // Получаем роли пользователя
            var roles = userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());

            userInfo.put("roles", roles);
            userInfo.put("isAdmin", roles.contains("ROLE_ADMIN"));
        } else {
            userInfo.put("authenticated", false);
        }

        return userInfo;
    }
}