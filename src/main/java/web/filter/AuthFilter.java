package web.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import web.security.JwtAuthentication;
import web.security.JwtProvider;
import web.security.JwtUtil;

import java.io.IOException;

public class AuthFilter extends GenericFilterBean {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    public AuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        try {
            // 1. получаем токен из заголовка Authorization ("Bearer {accessToken}")
            String token = getTokenFromRequest(request);

            // 2. валидируем access-токен с помощью JwtProvider
            if (token != null && jwtProvider.validateAccessToken(token)) {
                // 3. получаем claims с помощью JwtProvider
                Claims claims = jwtProvider.getClaims(token);

                // 4. с помощью JwtUtil создаем JwtAuthentication по claims
                JwtAuthentication jwtInfoToken = JwtUtil.generate(claims);
                jwtInfoToken.setAuthenticated(true);

                // 5. устанавливаем авторизацию с помощью SecurityContextHolder
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(jwtInfoToken);
                SecurityContextHolder.setContext(context);

                // сохраняем userId в атрибуты для обратной совместимости с GameController
                request.setAttribute("userId", jwtInfoToken.getUuid());
            }

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            // обязательная очистка контекста текущего потока пула Tomcat
            SecurityContextHolder.clearContext();
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}