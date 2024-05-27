package com.peak.main.controller;

import com.peak.main.model.User;
import com.peak.main.service.UserService;
import com.peak.security.model.RegisterRequest;
import com.peak.main.request.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
public class UserControl {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Response> getMe(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new Response(user));
    }

    @PutMapping("/me")
    public ResponseEntity<Response> updateMe(@RequestBody RegisterRequest requestUpdate, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(new Response(userService.update(user, requestUpdate)));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Response> deleteCustomer(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.delete(user);
        return ResponseEntity.ok(new Response("[]"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> getCustomers() {
        return ResponseEntity.ok(new Response(userService.getAllUsers()));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> update(@RequestBody RegisterRequest requestUpdate) {
        User customer = userService.findByName(requestUpdate.getName());
        if (customer == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().body(new Response(userService.update(customer, requestUpdate)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().body(new Response("[]"));
    }
}
