package com.gopikishan.mobileshop.config;

import com.gopikishan.mobileshop.entity.User;
import com.gopikishan.mobileshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public AdminInitializer(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String email = "admin@gmail.com";
        if (!repository.existsByEmail(email.toLowerCase())){
            User user = new User();
            user.setName("Admin");
            user.setEmail(email.toLowerCase());
            user.setPassword(encoder.encode(System.getenv("ADMIN_PASSWORD")));
            user.setRole("ADMIN");

            repository.save(user);
        }
    }
}
