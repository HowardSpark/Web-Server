package cn.edu.sdu.java.server.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 关闭CSRF（前后端分离必需）
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 引用统一的CORS配置
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**") // 所有/api/*路径都需要登录认证
                        .authenticated()
                        .anyRequest().permitAll() // 非/api路径放行（如登录接口/login）
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 无状态（JWT用）
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 统一的CORS跨域配置（删除重复配置，避免冲突）
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8005")); // 允许前端地址
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // 允许所有请求方法
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // 允许携带的请求头（含JWT的Authorization）
        configuration.setAllowCredentials(true); // 允许携带Cookie（可选，JWT不需要但建议开启）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 对所有路径生效
        return source;
    }
}