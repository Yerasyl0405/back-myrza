package com.example.demo.controller;


import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/hash-passwords")
    public String hashAllPasswords() {
        List<User> users = userRepository.findAll();
        int updated = 0;

        for (User user : users) {
            String rawPassword = user.getPassword();

            // Хешируем пароль только если он еще не хеширован
            if (!rawPassword.startsWith("$2a$")) {
                String hashedPassword = passwordEncoder.encode(rawPassword);
                user.setPassword(hashedPassword);
                userRepository.save(user);
                updated++;

                System.out.println("🔐 Хеширован пароль для " + user.getUsername() +
                        ": " + rawPassword + " -> " + hashedPassword);
            }
        }

        return "Обновлено паролей: " + updated;
    }
}