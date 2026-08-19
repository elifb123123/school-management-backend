package com.example.demo.auth.persistence;

import com.example.demo.user.persistence.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;


    // bir kullanıcının birden fazla refresh token'ı olabilir. Çünkü kullanıcı aynı anda birden fazla cihazdan giriş yapabilir. Bu yüzden ManyToOne ilişki kuruyoruz.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Instant expiryDate; // token'ın geçerlilik süresi. Bu süre dolduğunda token geçersiz olur ve kullanıcı tekrar giriş yapmak zorunda kalır.
    //  Instant konuma göre değişmeyen sabit bir noktaya göre zamanı temsil eder. Bu yüzden token'ın geçerlilik süresi için Instant kullanıyoruz. LocalDateTime kullanırsak, token'ın geçerlilik süresi sunucunun bulunduğu konuma göre değişebilir.
    private boolean revoked; // token'ın iptal edilip edilmediğini gösterir. Eğer token iptal edilmişse, kullanıcı tekrar giriş yapmak zorunda kalır. Log out durumu için.

}
