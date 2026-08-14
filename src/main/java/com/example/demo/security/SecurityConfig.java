package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
//projeden herhangi bir method çağırmadan önce @PreAuthorize varsa kontrol eder. Bunu yazmasaydık onları kontrol etmezdi, authanticated olan her kullanıcı çağırabilirdi.
public class SecurityConfig {

    @Bean

    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //post, put, delete, patch isteklerine izin vermek için bunu engellemek zorundayız.
                .authorizeHttpRequests(request ->
                        request.requestMatchers(HttpMethod.GET, "/api/school", "/api/teacher", "/api/student").hasRole("ADMIN")
                                .requestMatchers("/api/register/principal").permitAll()
                                .requestMatchers("/api/register/teacher").hasRole("PRINCIPAL")// sadece principal rolüne sahip kullanıcılar öğretmen ekleyebilir.
                                .requestMatchers("/api/register/student").hasRole("PRINCIPAL")// sadece principal rolüne sahip kullanıcılar öğrenci ekleyebilir.
                                .anyRequest().authenticated())// eğer bunu yazmazsak csrfyi disabled ettiğimiz için hiç bir kontrol yapmaz herşeye izin verir. Bu yüzden her isteğin authenticated olmasını istiyoruz.
                /*
                 * anyRequest().authenticated() giriş yapan herkes görebilir.
                 * anyRequest().permitAll() herkes görebilir.
                 * anyRequest().hasRole("ADMIN") sadece admin görebilir.
                 * requestMatchers("/api/**").authenticated() sadece /api/ ile başlayan endpointler için geçerli olur.
                 * .denyAll() hiçbir isteğe izin vermez. 403 döner.
                 * */

                .httpBasic(Customizer.withDefaults()) // http basic authentication kullanıyoruz. username ve password ile giriş yapıyoruz. bunu yazmazsak nasıl kontrol edeceğini bilemiyor 403 dönüyor.
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
