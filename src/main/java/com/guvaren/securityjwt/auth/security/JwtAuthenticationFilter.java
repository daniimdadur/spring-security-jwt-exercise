package com.guvaren.securityjwt.auth.security;

import com.guvaren.securityjwt.auth.service.CustomUserDetailsService;
import com.guvaren.securityjwt.auth.service.AccessJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final AccessJwtService accessJwtService;

    public JwtAuthenticationFilter(CustomUserDetailsService userDetailsService, AccessJwtService accessJwtService) {
        this.userDetailsService = userDetailsService;
        this.accessJwtService = accessJwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isAuthEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

//            if (!isTokenValidInDb(jwt)) {
//                log.debug("Token invalid or expired in database");
//                filterChain.doFilter(request, response);
//                return;
//            }

            String username = this.accessJwtService.extractAccessUsername(jwt);
            if (username != null && !isAlreadyAuthenticated()) {
                authenticateUser(request, jwt, username);
            }
        } catch (JwtException e) {
            log.error("JWT processing failed: {}", e.getMessage());
            handleJwtException(response, e);
            return;
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.contains("/api/v1/auth/register") ||
                path.contains("/api/v1/auth/login") ||
                path.contains("/api/v1/auth/refresh-token");
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String jwt = authHeader.substring(7);

        if (jwt.length() < 10) {
            return null;
        }

        return jwt;
    }

//    private boolean isTokenValidInDb(String jwt) {
//        return this.tokenRepo.findByToken(jwt)
//                .map(token -> !token.isRevoked() && !token.isExpired())
//                .orElse(false);
//    }

    private boolean isAlreadyAuthenticated() {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        return existingAuth != null && existingAuth.isAuthenticated();
    }

    private void authenticateUser(HttpServletRequest request, String jwt, String username) {
        UserDetails userDetails = loadUserDetailsWithCache(username);
        if (this.accessJwtService.isAccessTokenValid(jwt)) {
            UsernamePasswordAuthenticationToken authentication =
                    createAuthenticationToken(userDetails);

            //baris dibawah ini untuk keperluan audit atau logging
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private UserDetails loadUserDetailsWithCache(String username) {
        // Implement caching dengan @Cacheable di service layer
        return this.userDetailsService.loadUserByUsername(username);
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(
            UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,       // Principal adalah UserDetails object
                null,              // Credentials/password diset null untuk keamanan
                userDetails.getAuthorities()
        );
    }

    private void handleJwtException(HttpServletResponse response, JwtException e)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorMessage;
        if (e instanceof ExpiredJwtException) {
            errorMessage = "Token has expired";
        } else if (e instanceof MalformedJwtException) {
            errorMessage = "Invalid token format";
        } else {
            errorMessage = "Invalid token";
        }

        Map<String, String> body = Map.of(
                "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "message", errorMessage
        );

        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
