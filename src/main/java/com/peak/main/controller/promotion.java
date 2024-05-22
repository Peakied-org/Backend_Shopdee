package com.peak.main.controller;

import com.peak.main.Request.Response;
import com.peak.main.model.Promotion;
import com.peak.main.repository.PromotionRespository;
import com.peak.main.service.PromotionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotion")
@AllArgsConstructor
public class promotion {

    private final PromotionService promotionService;

//  /promotion
    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(promotionService.findAll()));
    }

/*  /promotion
    {
        "image":"http"
    }
 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> create(@RequestBody Promotion promotion) {
        if (promotion.getImage() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.status(201).body(new Response(promotionService.save(promotion)));
    }

//  /promotion
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        promotionService.deleteById(id);
        return ResponseEntity.ok(new Response("[]"));
    }
}
