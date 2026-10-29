package com.gopikishan.mobileshop.service;


import com.gopikishan.mobileshop.entity.User;
import com.gopikishan.mobileshop.entity.UserPrincipal;
import com.gopikishan.mobileshop.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repository.findByEmail(username).orElseThrow(
                () -> new RuntimeException("User not found " + username));

        return new UserPrincipal(user);
    }
}
