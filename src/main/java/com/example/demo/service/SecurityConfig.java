package com.example.demo.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailService userDetailsService;

    public SecurityConfig(MyUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/db/**", "/h2-console/**").permitAll() // разрешаем доступ к DB эндпоинтам
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                        .requestMatchers("/api/user/current").authenticated()
                        .requestMatchers("/api/breads").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            System.out.println("✅ УСПЕШНАЯ АУТЕНТИФИКАЦИЯ: " + authentication.getName());
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": true, \"message\": \"Login successful\", \"username\": \"" + authentication.getName() + "\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("❌ ОШИБКА АУТЕНТИФИКАЦИИ: " + exception.getMessage());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Login failed\", \"message\": \"" + exception.getMessage() + "\"}");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": true, \"message\": \"Logout successful\"}");
                        })
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Not authenticated\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Access denied\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // NoOpPasswordEncoder для паролей в чистом виде
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                boolean result = rawPassword.toString().equals(encodedPassword);
                System.out.println("🔐 СРАВНЕНИЕ: '" + rawPassword + "' == '" + encodedPassword + "' = " + result);
                return result;
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://myrza-nan.vercel.app",
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}