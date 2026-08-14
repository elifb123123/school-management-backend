package com.example.demo.school.security;

import com.example.demo.school.persistence.School;
import com.example.demo.school.persistence.SchoolRepository;
import com.example.demo.user.persistence.User;
import com.example.demo.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("schoolSecurity") // bağımlılık oluşturmadan bu isimle bulunmasını sağlar.
@RequiredArgsConstructor
public class SchoolSecurity {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;

    //bu fonksiyon @PreAuthorize'da kullanılmak için yazıldığı için true ya da false dönmek zorunda. O yüzden bulunamadığında access denied exception fırlatamıyoruz sadece false dönüyoruz.
    public boolean isPrincipalOf(String principalEmail, Long schoolId) {
        Optional<User> user = userRepository.findByEmail(principalEmail);
        if (user.isEmpty()) {
            return false;
        }
        Optional<School> school = schoolRepository.findByUser(user.get());
        if (school.isEmpty()) {
            return false;
        }
        return school.get().getId().equals(schoolId);
    }
}
