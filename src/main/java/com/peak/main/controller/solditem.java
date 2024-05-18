package com.peak.main.controller;

import com.peak.Util.Status;
import com.peak.main.Request.Response;
import com.peak.main.model.SoldItem;
import com.peak.main.repository.SoldItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/soldItem")
@AllArgsConstructor
public class solditem {

    private final SoldItemRepository soldItemRepository;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(soldItemRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<Response> create(@RequestBody SoldItem soldItem) {
        if (soldItem.getName() == null ||
                soldItem.getDescription() == null)
            return ResponseEntity.notFound().build();

        soldItem.setStatus(Status.ORDER);
        return ResponseEntity.ok(new Response(soldItemRepository.save(soldItem)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        soldItemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
