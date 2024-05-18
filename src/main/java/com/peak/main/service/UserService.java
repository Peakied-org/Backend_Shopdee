package com.peak.main.service;

import com.peak.main.model.User;
import com.peak.main.repository.UserRepository;
import com.peak.security.model.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByName(String name) {
        Optional<User> user = userRepository.findByName(name);
        return user.orElse(null);
    }

    public User update(User oldUser, RegisterRequest newUser) {
        if (newUser.getPassword() != null) oldUser.setPassword(newUser.getPassword());
        if (newUser.getTel() != null) oldUser.setTel(newUser.getTel());
        if (newUser.getAddress() != null) oldUser.setAddress(newUser.getAddress());
        if (newUser.getCard_number() != null) oldUser.setCard_number(newUser.getCard_number());
        return userRepository.save(oldUser);
    }

    public void delete(String name) {
        Optional<User> user = userRepository.findByName(name);
        if (user.isEmpty()) return;
        userRepository.delete(user.get());
    }
}
