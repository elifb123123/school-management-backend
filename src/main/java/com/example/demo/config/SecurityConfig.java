package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean

    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //post, put, delete, patch isteklerine izin vermek için bunu engellemek zorundayız.
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())// eğer bunu yazmazsak csrfyi disabled ettiğimiz için hiç bir kontrol yapmaz herşeye izin verir. Bu yüzden her isteğin authenticated olmasını istiyoruz.
                /*
                 * anyRequest().authenticated() giriş yapan herkes görebilir.
                 * anyRequest().permitAll() herkes görebilir.
                 * anyRequest().hasRole("ADMIN") sadece admin görebilir.
                 * anyRequest().requestMatchers("/api/**").authenticated() sadece /api/ ile başlayan endpointler için geçerli olur.
                 * .denyAll() hiçbir isteğe izin vermez. 403 döner.
                 * */

                .httpBasic(Customizer.withDefaults()) // http basic authentication kullanıyoruz. username ve password ile giriş yapıyoruz. bunu yazmazsak nasıl kontrol edeceğini bilemiyor 403 dönüyor.
                .build();
    }

//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
