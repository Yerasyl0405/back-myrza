package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public MyUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 ЗАПРОС ПОЛЬЗОВАТЕЛЯ: '" + username + "' (длина: " + username.length() + ")");

        // Пробуем оба метода
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    System.out.println("🔄 Пробуем native query...");
                    return userRepository.findByUsernameNative(username).orElse(null);
                });

        if (user == null) {
            // Выводим всех пользователей для отладки
            List<User> allUsers = userRepository.findAll();
            System.out.println("📋 ВСЕ ПОЛЬЗОВАТЕЛИ В БАЗЕ:");
            allUsers.forEach(u -> {
                System.out.println("   👤 '" + u.getUsername() + "' (длина: " + u.getUsername().length() + ")");
            });

            System.out.println("❌ ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН: '" + username + "'");
            throw new UsernameNotFoundException("User not found: '" + username + "'");
        }

        System.out.println("✅ ПОЛЬЗОВАТЕЛЬ НАЙДЕН: " + user.getUsername());
        System.out.println("🔑 ПАРОЛЬ В БАЗЕ: '" + user.getPassword() + "'");
        System.out.println("🎭 РОЛЬ: '" + user.getRole() + "'");

        // Формируем роль с префиксом ROLE_
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );

        System.out.println("🎯 Authorities: " + authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}