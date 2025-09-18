package example.spring_template.config;

import example.spring_template.auth.filter.JsonUsernamePasswordAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        JsonUsernamePasswordAuthFilter jsonFilter = new JsonUsernamePasswordAuthFilter();
        jsonFilter.setAuthenticationManager(authManager);

        http
            .csrf(csrf -> csrf.disable()) // API 개발 단계. csrf 비활성화, 보안 설정 필요
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/login", "/public/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable()) // 폼 로그인 비활성화
            .logout(logout -> logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler((req, res, authn) -> {
                        res.setContentType("application/json");
                        res.getWriter().write("{\"success\":true,\"message\":\"logout ok\"}");
                    })
            )
                .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // 기본 DaoAuthProvider 구성 사용
    }
}