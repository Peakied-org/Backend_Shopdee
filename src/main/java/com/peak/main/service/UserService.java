package com.peak.main.service;

import com.peak.main.model.User;
import com.peak.main.repository.UserRepository;
import com.peak.security.model.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User update(User oldUser, RegisterRequest newUser) {
        if (newUser.getPassword() != null) oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if (newUser.getTel() != null) oldUser.setTel(newUser.getTel());
        if (newUser.getAddress() != null) oldUser.setAddress(newUser.getAddress());
        if (newUser.getCardNumber() != null) oldUser.setCardNumber(newUser.getCardNumber());
        return userRepository.save(oldUser);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
