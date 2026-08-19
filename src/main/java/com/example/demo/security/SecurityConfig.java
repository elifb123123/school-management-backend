package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
//projeden herhangi bir method çağırmadan önce @PreAuthorize varsa kontrol eder. Bunu yazmasaydık onları kontrol etmezdi, authanticated olan her kullanıcı çağırabilirdi.
public class SecurityConfig {

    @Bean

    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //post, put, delete, patch isteklerine izin vermek için bunu engellemek zorundayız.
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/school", "/api/teacher", "/api/student").hasRole("ADMIN")
                                .requestMatchers("/api/register/principal").permitAll()
                                .requestMatchers("/api/register/teacher").hasRole("PRINCIPAL")// sadece principal rolüne sahip kullanıcılar öğretmen ekleyebilir.
                                .requestMatchers("/api/register/student").hasRole("PRINCIPAL")// sadece principal rolüne sahip kullanıcılar öğrenci ekleyebilir.
                                .anyRequest().authenticated())// eğer bunu yazmazsak csrfyi disabled ettiğimiz için hiç bir kontrol yapmaz herşeye izin verir. Bu yüzden her isteğin authenticated olmasını istiyoruz.

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // addFilter sadece  sadece Spring Security'nin kendi tanıdığı, bilinen filtre tiplerini kabul ediyor.
                // addFilterBefore ve addFilterAfter ise kendi yazdığımız filtreleri de ekleyebiliyoruz. addFilterBefore ile eklediğimiz filtreyi, Spring Security'nin kendi filtrelerinden önce çalıştırıyoruz.
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
