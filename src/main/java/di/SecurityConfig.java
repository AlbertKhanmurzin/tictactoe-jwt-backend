package di;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import web.filter.AuthFilter;
import web.security.JwtProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // внедряем JwtProvider в наш фильтр
    @Bean
    public AuthFilter authFilter(JwtProvider jwtProvider) {
        return new AuthFilter(jwtProvider);
    }

    // отключаем регистрацию фильтра в Tomcat, чтобы он выполнялся только в Spring Security
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(AuthFilter filter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight OPTIONS запросы от фронтенда
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // доступ без авторизации к эндпоинтам логина, регистрации и обновления токенов
                        .requestMatchers("/api/auth/**").permitAll()
                        // статические файлы веб-интерфейса
                        .requestMatchers("/", "/index.html", "/style.css", "/app.js", "/static/**", "/favicon.ico").permitAll()
                        // все остальные эндпоинты (игры, профиль и т.д.) требуют валидный accessToken
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                )
                // ставим фильтр перед стандартным UsernamePasswordAuthenticationFilter
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}