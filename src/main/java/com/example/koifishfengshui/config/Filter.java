package com.example.koifishfengshui.config;

import com.example.koifishfengshui.exception.AuthException;
import com.example.koifishfengshui.model.entity.Account;
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
            "/api/auth/google",
            "/api/password/forgot",
            "/api/password/reset",
            "/api/ads/vn-pay-callback",
            "/api/cloudinary/upload"
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

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());
        if (isPublicAPI) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        if (token == null) {
            handlerExceptionResolver.resolveException(request, response, null,
                    new AuthException("Empty Token!!!"));
            return;
        }

        try {
            Account account = tokenService.getAccountByToken(token);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(account, token, account.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response); // Tiếp tục xử lý nếu token hợp lệ.
        } catch (AuthException e) { // Thêm bắt AuthException để xử lý token bị blacklist.
            handlerExceptionResolver.resolveException(request, response, null, e);
        } catch (ExpiredJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null,
                    new AuthException("Expired Token!!!"));
        } catch (MalformedJwtException malformedJwtException) {
            handlerExceptionResolver.resolveException(request, response, null,
                    new AuthException("Invalid Token!!!"));
        }
    }


}

