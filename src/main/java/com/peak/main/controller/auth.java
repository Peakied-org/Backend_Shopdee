package com.peak.main.controller;

import com.peak.security.model.AuthenticationRequest;
import com.peak.security.service.AuthenticationService;
import com.peak.security.model.RegisterRequest;
import com.peak.main.Request.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class auth {

    private final AuthenticationService service;

    // /api/v1/auth/register
    // {
    //    "name":"admin",
    //    "password":"12345678",
    //    "key":"password" if add key be admin
    // }
    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest request) {
        if (request.getName() == null ||
                request.getPassword() == null ||
                request.getTel() == null ||
                request.getAddress() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new Response(service.register(request)));
    }

    // /api/v1/auth/authenticate
    // {
    //    "name":"admin",
    //    "password":"12345678"
    // }
    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(@RequestBody AuthenticationRequest request) {
        if (request.getName() == null || request.getPassword() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new Response(service.authenticate(request)));
    }
}
