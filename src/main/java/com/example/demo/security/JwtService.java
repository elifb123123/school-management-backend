package com.example.demo.security;

import com.example.demo.user.persistence.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public SecretKey getSignInKey() { // String olan key'i önce byte array'e sonra SecretKey nesnesine çeviriyoruz.
        byte[] secretKeyBytes = Decoders.BASE64.decode(this.secretKey);
        return hmacShaKeyFor(secretKeyBytes);
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .signWith(getSignInKey())
                .claim("role", user.getRole().name()) //Security Config'deki kontroller için buradan bakabilir.
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 dakika
                .compact(); // string dönmesini sağlar.
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject(); //subject olarak generatede email vermiştik.

    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()//JwtParser nesnesi oluşturuyoruz. parseSignedClaims(token) fonksiyonu bu nesneyle çalışıyor.
                .parseSignedClaims(token)//Kontrol burada yapılıyor. uyuşma olmazsa hata atar. JWS(Doğrulanmış JWT) döner.
                .getPayload();
    }
}
