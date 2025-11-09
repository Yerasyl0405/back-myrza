package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check-usernames")
    public String checkUsernames() {
        StringBuilder result = new StringBuilder();
        result.append("=== ПРОВЕРКА ИМЕН ПОЛЬЗОВАТЕЛЕЙ В БАЗЕ ===\n");

        // Проверяем всех пользователей через raw SQL
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT id, username, password, role FROM users"
        );

        for (Map<String, Object> user : users) {
            String username = (String) user.get("username");
            result.append(String.format(
                    "👤 ID: %s, Username: '%s' (длина: %d), Password: '%s', Role: '%s'\n",
                    user.get("id"),
                    username,
                    username.length(),
                    user.get("password"),
                    user.get("role")
            ));

            // Проверяем через JPA
            try {
                User jpaUser = userRepository.findByUsername(username).orElse(null);
                if (jpaUser != null) {
                    result.append("   ✅ JPA находит этого пользователя\n");
                } else {
                    result.append("   ❌ JPA НЕ находит этого пользователя!\n");
                }
            } catch (Exception e) {
                result.append("   💥 Ошибка JPA: " + e.getMessage() + "\n");
            }
        }

        // Проверяем конкретно erasil
        result.append("\n=== ПРОВЕРКА КОНКРЕТНО 'erasil' ===\n");
        try {
            User erasilUser = userRepository.findByUsername("erasil").orElse(null);
            if (erasilUser != null) {
                result.append("✅ JPA находит 'erasil'\n");
            } else {
                result.append("❌ JPA НЕ находит 'erasil'\n");

                // Проверяем через raw SQL
                List<Map<String, Object>> erasilRaw = jdbcTemplate.queryForList(
                        "SELECT * FROM users WHERE username = ?", "erasil"
                );
                result.append("Raw SQL находит 'erasil': " + !erasilRaw.isEmpty() + "\n");
            }
        } catch (Exception e) {
            result.append("💥 Ошибка при проверке 'erasil': " + e.getMessage() + "\n");
        }

        System.out.println(result.toString());
        return result.toString();
    }
}