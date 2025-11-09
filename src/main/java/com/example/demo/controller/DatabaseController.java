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
            System.out.println("=== ПРОВЕРКА СТАТУСА БАЗЫ ДАННЫХ ===");

            // 1. Простой тест подключения
            String testQuery = "SELECT 1 as test_value";
            Integer testResult = jdbcTemplate.queryForObject(testQuery, Integer.class);
            System.out.println("✅ Тест подключения: " + testResult);

            // 2. Проверяем существующие таблицы
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = 'public' ORDER BY table_name"
            );

            System.out.println("📊 Найдено таблиц: " + tables.size());
            tables.forEach(table -> {
                System.out.println("   📋 " + table.get("table_name"));
            });

            // 3. Проверяем таблицу users если она существует
            boolean usersTableExists = tables.stream()
                    .anyMatch(t -> "users".equals(t.get("table_name")));

            List<Map<String, Object>> users = List.of();
            if (usersTableExists) {
                users = jdbcTemplate.queryForList("SELECT * FROM users ORDER BY id");
                System.out.println("👥 Пользователей в таблице: " + users.size());
                users.forEach(u -> {
                    System.out.println("   👤 ID: " + u.get("id") +
                            ", Username: '" + u.get("username") + "'" +
                            ", Password: '" + u.get("password") + "'" +
                            ", Role: '" + u.get("role") + "'");
                });
            } else {
                System.out.println("❌ Таблица users не существует");
            }

            return Map.of(
                    "connectionTest", testResult,
                    "tablesCount", tables.size(),
                    "tables", tables,
                    "usersTableExists", usersTableExists,
                    "users", users,
                    "usersCount", users.size(),
                    "status", "SUCCESS"
            );

        } catch (Exception e) {
            System.out.println("❌ Ошибка при проверке статуса: " + e.getMessage());
            e.printStackTrace();
            return Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            );
        }
    }

    @PostMapping("/reset-users")
    public Map<String, Object> resetUsersTable() {
        try {
            System.out.println("=== СОЗДАНИЕ/СБРОС ТАБЛИЦЫ USERS ===");

            // 1. Удаляем таблицу если существует
            jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
            System.out.println("✅ Таблица users удалена");

            // 2. Создаем таблицу заново
            String createTableSQL =
                    "CREATE TABLE users (" +
                            "id BIGSERIAL PRIMARY KEY, " +
                            "username VARCHAR(50) UNIQUE NOT NULL, " +
                            "password VARCHAR(100) NOT NULL, " +
                            "role VARCHAR(50) NOT NULL)";
            jdbcTemplate.execute(createTableSQL);
            System.out.println("✅ Таблица users создана");

            // 3. Добавляем тестовых пользователей
            String[][] testUsers = {
                    {"admin", "admin", "ADMIN"},
                    {"user", "user", "USER"},
                    {"guest", "guest", "USER"}
            };

            int usersAdded = 0;
            for (String[] userData : testUsers) {
                jdbcTemplate.update(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                        userData[0], userData[1], userData[2]
                );
                System.out.println("✅ Добавлен пользователь: " + userData[0]);
                usersAdded++;
            }

            // 4. Проверяем результат
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM users ORDER BY id");
            System.out.println("👥 Итоговые пользователи: " + users.size());

            return Map.of(
                    "status", "SUCCESS",
                    "message", "Таблица users успешно создана и заполнена",
                    "usersAdded", usersAdded,
                    "totalUsers", users.size(),
                    "users", users
            );

        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании таблицы: " + e.getMessage());
            e.printStackTrace();
            return Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            );
        }
    }

    @GetMapping("/simple-test")
    public Map<String, Object> simpleTest() {
        try {
            // Самый простой тест - проверяем что можем выполнить запрос
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            return Map.of(
                    "status", "SUCCESS",
                    "databaseTest", result,
                    "message", "База данных доступна"
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            );
        }
    }

    @GetMapping("/check-users")
    public Map<String, Object> checkUsers() {
        try {
            // Проверяем существует ли таблица users
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = 'public' AND table_name = 'users'"
            );

            boolean usersTableExists = !tables.isEmpty();
            List<Map<String, Object>> users = List.of();

            if (usersTableExists) {
                users = jdbcTemplate.queryForList("SELECT * FROM users ORDER BY id");
            }

            return Map.of(
                    "usersTableExists", usersTableExists,
                    "usersCount", users.size(),
                    "users", users
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            );
        }
    }
}