package com.example.koifishfengshui.config;

import com.example.koifishfengshui.model.entity.Account;
import com.example.koifishfengshui.exception.AuthException;
import com.example.koifishfengshui.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {
    private final List<String> AUTH_PERMISSION = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/auth/register",
            "/api/auth/login",
            "/api/password/forgot",
            "/api/password/reset",
            "/api/ads/vn-pay-callback"
    );

    @Autowired
    TokenService tokenService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver handlerExceptionResolver;

    public boolean checkIsPublicAPI(String uri) {
        //uri: /api/register

        // nếu gặp những API trong List AUTH_PERMISSION -> cho phép truy cập -> true
        // else -> false
        AntPathMatcher pathMatcher = new AntPathMatcher();

        return AUTH_PERMISSION.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Kiểm tra trước khi cho phép truy cập vào controller
        // check xem API mà ng dùng yêu cầu có phải là public API hay không?
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());
        if (isPublicAPI) {
            filterChain.doFilter(request, response);
        } else {
            String token = getToken(request);
            if (token == null) {
                // cant access to this API
                handlerExceptionResolver.resolveException(request, response, null, new AuthException("Empty Token!!!"));
                return;
            }

            // => có token
            // check xem token có đúng hay không -> lấy account's inf từ token
            Account account;
            try {
                account = tokenService.getAccountByToken(token);
            } catch (ExpiredJwtException e) {
                // response token expired
                handlerExceptionResolver.resolveException(request, response, null, new AuthException("Expired Token!!!"));
                return;
            } catch (MalformedJwtException malformedJwtException) {
                // response invalid token
                handlerExceptionResolver.resolveException(request, response, null, new AuthException("Invalid Token!!!"));
                return;
            }
            // token juan -> can access API
            //store account to SecurityContextHolder to help Controller identify Account
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    account,
                    token,
                    account.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        }
    }
}

