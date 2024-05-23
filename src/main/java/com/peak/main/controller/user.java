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
    public ResponseEntity<Response> getMe(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new Response(user));
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
    public ResponseEntity<Response> updateMe(@RequestBody RegisterRequest requestUpdate, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(new Response(userService.update(user, requestUpdate)));
    }

//  /api/v1/customers/me
    @DeleteMapping("/me")
    public ResponseEntity<Response> deleteCustomer(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.delete(user);
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
        if (customer == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().body(new Response(userService.update(customer, requestUpdate)));
    }

/*
    /api/v1/customers
    {
        "name":"name"
    }
 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().body(new Response("[]"));
    }
}
