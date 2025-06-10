package hexlet.code.app.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey key;
    // 🕒 Время жизни токена — например, 1 день
    private static final long EXPIRATION = ChronoUnit.DAYS.getDuration().toMillis();
    public static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
    @PostConstruct
    public void init() {
        // base64 decode ключа и создаём ключ из него
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
    public String generateToken(String email) {
        LOGGER.info("JWT Key: {}", key.toString());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(Date.from(Instant.now().plusSeconds(86400)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
