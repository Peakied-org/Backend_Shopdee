package com.peak.main.controller;

import com.peak.util.Role;
import com.peak.util.Status;
import com.peak.main.request.Response;
import com.peak.main.model.OrderDetails;
import com.peak.main.model.User;
import com.peak.main.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderControl {

    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Response> getOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Role userRole = user.getRole();
        List<?> orders;
        switch (userRole) {
            case ADMIN:
                orders = orderService.getAllOrders();
                break;
            case SELLER:
                try {
                    orders = orderService.getOrderDetailsBySeller(user);
                } catch (EntityNotFoundException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response("Store not found for user"));
                }
                break;
            case USER:
                orders = orderService.getOrdersByUserId(user.getId());
                break;
            default:
                return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(new Response(orders));
    }

    @PostMapping
    public ResponseEntity<Response> createOrderFromCart(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            com.peak.main.model.Order order = orderService.createOrderFromCart(user.getId());
            return ResponseEntity.ok(new Response(order));
        } catch (EntityNotFoundException | IllegalArgumentException ex) {
            return ResponseEntity.status(ex instanceof EntityNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST)
                    .body(new Response(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response("An error occurred while creating the order."));
        }
    }

    @PutMapping("/{orderDetailId}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Response> updateOrderDetailStatus(
            @PathVariable Long orderDetailId,
            @RequestParam Status status,
            Authentication authentication
    ) {
        try {
            User user = (User) authentication.getPrincipal();
            if (user.getRole() == Role.SELLER && !orderService.isOrderDetailOwnedBySeller(orderDetailId, user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new Response("You are not authorized to update this order detail."));
            }
            OrderDetails updatedOrderDetail = orderService.updateOrderDetailStatus(orderDetailId, status);
            return ResponseEntity.ok(new Response(updatedOrderDetail));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(e.getMessage()));
        }
    }
}

