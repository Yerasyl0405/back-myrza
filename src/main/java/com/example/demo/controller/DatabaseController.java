package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/status")
    public Map<String, Object> getDatabaseStatus() {
        try {
            // 1. Проверяем подключение
            String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            String dbUser = jdbcTemplate.queryForObject("SELECT current_user", String.class);

            // 2. Проверяем существующие таблицы
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = 'public' ORDER BY table_name"
            );

            // 3. Проверяем таблицу users если она существует
            boolean usersTableExists = tables.stream()
                    .anyMatch(t -> "users".equals(t.get("table_name")));

            List<Map<String, Object>> users = List.of();
            if (usersTableExists) {
                users = jdbcTemplate.queryForList("SELECT * FROM users ORDER BY id");
            }

            System.out.println("=== СТАТУС БАЗЫ ДАННЫХ ===");
            System.out.println("📊 База: " + dbName);
            System.out.println("👤 Пользователь: " + dbUser);
            System.out.println("📋 Таблицы: " + tables.size());
            tables.forEach(t -> System.out.println("   - " + t.get("table_name")));
            System.out.println("👥 Таблица users существует: " + usersTableExists);
            System.out.println("👥 Пользователей в таблице: " + users.size());
            users.forEach(u -> System.out.println("   👤 " + u));

            return Map.of(
                    "database", dbName,
                    "user", dbUser,
                    "tables", tables,
                    "usersTableExists", usersTableExists,
                    "users", users,
                    "usersCount", users.size()
            );

        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @PostMapping("/reset-users")
    public String resetUsersTable() {
        try {
            System.out.println("=== СОЗДАНИЕ/СБРОС ТАБЛИЦЫ USERS ===");

            // 1. Удаляем таблицу если существует
            jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
            System.out.println("✅ Таблица users удалена");

            // 2. Создаем таблицу заново
            jdbcTemplate.execute(
                    "CREATE TABLE users (" +
                            "id BIGSERIAL PRIMARY KEY, " +
                            "username VARCHAR(50) UNIQUE NOT NULL, " +
                            "password VARCHAR(100) NOT NULL, " +
                            "role VARCHAR(50) NOT NULL)"
            );
            System.out.println("✅ Таблица users создана");

            // 3. Добавляем тестовых пользователей
            String[][] testUsers = {
                    {"admin", "admin", "ADMIN"},
                    {"user", "user", "USER"},
                    {"guest", "guest", "USER"}
            };

            for (String[] userData : testUsers) {
                jdbcTemplate.update(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                        userData[0], userData[1], userData[2]
                );
                System.out.println("✅ Добавлен пользователь: " + userData[0]);
            }

            // 4. Проверяем результат
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM users");
            System.out.println("👥 Итоговые пользователи:");
            users.forEach(u -> {
                System.out.println("   👤 ID: " + u.get("id") +
                        ", Username: " + u.get("username") +
                        ", Password: " + u.get("password") +
                        ", Role: " + u.get("role"));
            });

            return "Таблица users успешно создана и заполнена! Пользователей: " + users.size();

        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
            return "Ошибка: " + e.getMessage();
        }
    }

    @GetMapping("/test-query")
    public String testUserQuery() {
        try {
            // Пробуем найти пользователя разными способами
            List<Map<String, Object>> allUsers = jdbcTemplate.queryForList("SELECT * FROM users");
            List<Map<String, Object>> adminUser = jdbcTemplate.queryForList(
                    "SELECT * FROM users WHERE username = 'admin'"
            );

            System.out.println("=== ТЕСТ ЗАПРОСОВ ===");
            System.out.println("👥 Все пользователи: " + allUsers.size());
            allUsers.forEach(u -> System.out.println("   👤 " + u));
            System.out.println("👑 Пользователь admin: " + adminUser.size());
            adminUser.forEach(u -> System.out.println("   👑 " + u));

            return "All users: " + allUsers.size() + ", Admin found: " + adminUser.size();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}