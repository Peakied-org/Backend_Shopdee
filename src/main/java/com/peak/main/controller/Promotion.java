package com.peak.main.controller;

import com.peak.main.request.Response;
import com.peak.main.service.PromotionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotion")
@AllArgsConstructor
public class Promotion {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(promotionService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> create(@RequestBody com.peak.main.model.Promotion promotion) {
        if (promotion.getImage() == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.status(201).body(new Response(promotionService.save(promotion)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        promotionService.deleteById(id);
        return ResponseEntity.ok(new Response("[]"));
    }
}
