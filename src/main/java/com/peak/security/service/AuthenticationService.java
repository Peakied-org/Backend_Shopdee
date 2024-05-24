package com.peak.security.service;

import com.peak.util.Role;
import com.peak.main.model.User;
import com.peak.main.repository.UserRepository;
import com.peak.security.model.RegisterRequest;
import com.peak.security.model.AuthenticationRequest;
import com.peak.security.model.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${ADMIN_KEY}")
    private String adminKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Object register(RegisterRequest request) {
        var foundcustomer = userRepository.findByName(request.getName());
        if (foundcustomer.isPresent()) return "Username already register";

        User user = User.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .tel(request.getTel())
                .address(request.getAddress())
                .cardNumber(request.getCardNumber())
                .build();

        if (request.getCardNumber() != null) user.setRole(Role.SELLER);
        if (request.getKey() != null && request.getKey().equals(adminKey)) user.setRole(Role.ADMIN);

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public Object authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getName(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByName(request.getName()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}
