package hexlet.code.config;

import hexlet.code.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    
    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        var existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null
                && !(existingAuth instanceof AnonymousAuthenticationToken)
                && existingAuth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            log.info("Token received: {}", token);
            try {
                final String email = jwtUtils.extractEmail(token);
                var authentication = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException ex) {
                // токен истёк → 401 + JSON
                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT expired");
                return;
            } catch (JwtException | IllegalArgumentException ex) {
                // любые другие проблемы с JWT → 401 + JSON
                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            } catch (Exception e) {
                log.error("JWT authentication failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        } else {
            log.warn("No Authorization header or invalid format");
        }
        filterChain.doFilter(request, response);
    }
    
    private void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
