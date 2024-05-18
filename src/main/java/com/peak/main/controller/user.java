package com.peak.main.controller;

import com.peak.main.model.User;
import com.peak.main.service.UserService;
import com.peak.security.model.RegisterRequest;
import com.peak.main.Request.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class user {

    private final UserService userService;

//  /api/v1/customers/me
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    public ResponseEntity<Response> getMe(Authentication authentication) {
        return ResponseEntity.ok(new Response(userService.findByName(authentication.getName())));
    }

/*  /api/v1/customers/me
    {
        "password":"password"       // could be null
        "tel":"tel"                 // could be null
        "address":"address"         // could be null
        "card_number":"card_number" // could be null
    }
 */
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    public ResponseEntity<Response> updateMe(@RequestBody RegisterRequest requestUpdate, Authentication authentication) {
        return ResponseEntity.ok().body(new Response(userService.update(userService.findByName(authentication.getName()), requestUpdate)));
    }

//  /api/v1/customers/me
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    public ResponseEntity<Response> deleteCustomer(Authentication authentication) {
        userService.delete(authentication.getName());
        return ResponseEntity.ok(new Response("[]"));
    }

//  /api/v1/customers
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getCustomers() {
        return ResponseEntity.ok(new Response(userService.getAllUsers()));
    }

/*  /api/v1/customers
    {
        "name":"name"
        "password":"password"       // could be null
        "tel":"tel"                 // could be null
        "address":"address"         // could be null
        "card_number":"card_number" // could be null
    }
 */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> update(@RequestBody RegisterRequest requestUpdate) {
        User customer = userService.findByName(requestUpdate.getName());
        if (customer == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(new Response(userService.update(customer, requestUpdate)));
    }

/*
    /api/v1/customers
    {
        "name":"name"
    }
 */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@RequestBody RegisterRequest requestUpdate) {
        userService.delete(requestUpdate.getName());
        return ResponseEntity.ok().body(new Response(""));
    }
}
