package com.example.demo.auth.service;

import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.auth.dto.UserDto;
import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.repository.UserEntityRepository;
import com.example.demo.shared.exception.CustomException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService implements UserDetailsService {

    // @Autowired
    private final UserEntityRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<UserEntity> user = repository.findByEmail(email);

        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(email);
        }

    }

    // REGISTRATION SERVICE
    public UserEntity register(UserDto userDto) {
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new CustomException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return repository.save(user);
    }

    public UserEntity findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Account  Not Found"));
    }

    // LOGIN METHOD

}
