package com.peak.main.controller;

import com.peak.main.model.Cart;
import com.peak.main.request.Response;
import com.peak.main.model.User;
import com.peak.main.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartControl {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Response> getUserCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Cart cart = cartService.getCartsByUserId(user.getId());
        return ResponseEntity.ok(new Response(cart));
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<Response> addCartItem(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String type,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity. Quantity must be greater than 0.");
            }
            Cart updatedCart = cartService.addToCart(user.getId(), itemId, quantity, type);
            return  ResponseEntity.status(201).body(new Response(updatedCart));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Item not found with ID: " + itemId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(e.getMessage()));
        }
    }

    @PutMapping("/{cartDetailsId}")
    public ResponseEntity<Response> updateCartItem(
            @PathVariable Long cartDetailsId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();

            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(new Response("Invalid quantity. Quantity must be greater than 0."));
            }

            Cart updatedCart = cartService.updateCart(user.getId(), cartDetailsId, quantity);
            return ResponseEntity.ok(new Response(updatedCart));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(e.getMessage()));
        }
    }

    @DeleteMapping("/{cartDetailsId}")
    public ResponseEntity<Response> deleteCartItem(@PathVariable Long cartDetailsId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            cartService.deleteFromCart(user.getId(), cartDetailsId);
            return ResponseEntity.ok(new Response("Item removed from cart"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(e.getMessage()));
        }
    }
}
