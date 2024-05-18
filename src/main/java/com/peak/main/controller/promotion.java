package com.peak.main.controller;

import com.peak.main.Request.Response;
import com.peak.main.model.Promotion;
import com.peak.main.repository.PromotionRespository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotion")
@AllArgsConstructor
public class promotion {

    private final PromotionRespository promotionRespository;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(promotionRespository.findAll()));
    }

    @PostMapping
    public ResponseEntity<Response> create(@RequestBody Promotion promotion) {
        if (promotion.getImage() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new Response(promotionRespository.save(promotion)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        promotionRespository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
