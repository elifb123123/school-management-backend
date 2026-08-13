package com.example.demo.security;

import com.example.demo.user.persistence.User;
import com.example.demo.user.persistence.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder() //İki user sinifi olduğu için karışmaması için yolu ile yazdık.
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name()) // toString() yerine name() tercih ettik çünkü toString fonksiyonu override edilebilir ama name override edilemez bu yüzden daha güvenlidir.
                .build();
    }
}
