package com.peak.main.controller;

import com.peak.main.Request.Response;
import com.peak.main.model.Store;
import com.peak.main.repository.StoreRepository;
import com.peak.main.service.StoreService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@AllArgsConstructor
public class store {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(storeService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@RequestParam Long id) {
        return ResponseEntity.ok(new Response(storeService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Response> add(@RequestBody Store store) {
        if (store.getName() == null ||
                store.getUserID() == null ||
                store.getDetail() == null ||
                store.getImage() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new Response(storeService.save(store)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        storeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
