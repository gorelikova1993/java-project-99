package hexlet.code.controller;

import hexlet.code.dto.LoginRequestDto;
import hexlet.code.util.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private  AuthenticationManager authenticationManager;
    private  JwtUtils jwtUtils;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto) {
        var authInputToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        // Аутентифицируем
        Authentication authentication = authenticationManager.authenticate(authInputToken);
        // Важно! Устанавливаем аутентификацию в контекст
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Генерация токена
        String token = jwtUtils.generateToken(dto.getEmail());
        return ResponseEntity.ok(token);
    }
}
