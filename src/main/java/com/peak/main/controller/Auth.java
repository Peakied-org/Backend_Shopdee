package com.peak.main.controller;

import com.peak.security.model.AuthenticationRequest;
import com.peak.security.service.AuthenticationService;
import com.peak.security.model.RegisterRequest;
import com.peak.main.request.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class Auth {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest request) {
        if (request.getName() == null ||
                request.getPassword() == null ||
                request.getTel() == null ||
                request.getAddress() == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.status(201).body(new Response(service.register(request)));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(@RequestBody AuthenticationRequest request) {
        if (request.getName() == null || request.getPassword() == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(new Response(service.authenticate(request)));
    }
}
