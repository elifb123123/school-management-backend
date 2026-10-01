package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


//Tipik bir istekte aynı anda şunlar da bulunur:
//
//  - Content-Type — body'nin formatı ne (application/json gibi)
//  - Accept — client hangi formatta cevap istiyor
//  - Host — hangi sunucuya gidiyor
//  - User-Agent — isteği hangi yazılım/tarayıcı gönderdi
//  - Authorization — bizim ilgilendiğimiz, kimlik bilgisi

        String authHeader = request.getHeader("Authorization"); //example: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLnNtaXRoQGV4YW1wbGUuY29tIiwicm9sZSI6IlRFQUNIRVIiLCJleHAiOjE3MjM5ODQ5MDB9.k7x9F2s..."

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // git, her şeyi yaptır, bitince bana geri dön
            // filterChain, "montaj hattında benden sonraki tüm istasyonlar" anlamına geliyor — kalan filtreler ve en sonunda asıl controller. .doFilter(request, response) çağırmak, "ben işimi bitirdim, isteği bir
            // sonraki istasyona gönder" demek.
            return;
        }

        String token = authHeader.substring(7); //String.substring(int beginIndex) — bir string metodu, "bana şu index'ten itibaren, string'in sonuna kadar olan kısmı ver" diyor. Index'ler 0'dan başlar.
        // "Bearer " kısmı 7 karakter, 0-6 arası bearer kısmını kapsıyor, 7'den itibaren token başlıyor. Bu yüzden 7'den itibaren alıyoruz.

        try {
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            //UsernamePasswordAuthenticationToken,Authentication arayüzünün hazır bir implementasyonu.
            // Authentication nesnesini @PreAuthorize, hasRole(...), authentication.name gibi yerlerde kullanıyoruz.
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // token geçersiz/süresi dolmuş — SecurityContext'e hiçbir şey yazmadan devam
        }

        filterChain.doFilter(request, response);
    }
}
