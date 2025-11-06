package com.example.demo.user;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean register(String username, String password, String email) {
        try {
            // 중복 체크
            if (userRepository.findByUsername(username).isPresent()) {
                return false;
            }

            // 새 사용자 객체 생성 및 저장
            Users newUser = new Users();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setEmail(email);

            Users savedUser = userRepository.save(newUser);
            return savedUser != null && savedUser.getId() != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
