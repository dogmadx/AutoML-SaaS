package me.osipshmel.mediator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import me.osipshmel.mediator.repository.entity.User;
import me.osipshmel.mediator.repository.TokenRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${security.jwt.acces_token_expiration}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;
    private final SecretKey secretKey;
    private final TokenRepository tokenRepository;

    public JwtService(SecretKey secretKey, TokenRepository tokenRepository){
        this.secretKey = secretKey;
        this.tokenRepository = tokenRepository;
    }


    private String generateToken(@NonNull User user, long expiryTime){
        long cur_time = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(cur_time))
                .expiration(new Date(cur_time+expiryTime))
                .signWith(secretKey);

        return builder.compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration);
    }
    public String generateRefreshToken(User user){
        return generateToken(user, refreshTokenExpiration);
    }

    private Claims extractAllClaims(String token) {

        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(secretKey);

        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, @NonNull Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private boolean isAccessTokenNotExpired(String token) {
        return extractExpiration(token).after(new Date());
    }

    public boolean isValidAccess(String token, @NonNull UserDetails user) {

        String username = extractUsername(token);

        boolean isValidToken = tokenRepository.findByAccessToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isAccessTokenNotExpired(token)
                && isValidToken;
    }
    public boolean isValidRefresh(String token, @NonNull User user) {

        String username = extractUsername(token);

        boolean isValidRefreshToken = tokenRepository.findByRefreshToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isAccessTokenNotExpired(token)
                && isValidRefreshToken;
    }
}