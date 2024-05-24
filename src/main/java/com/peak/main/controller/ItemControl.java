package com.peak.main.controller;

import com.peak.main.request.Response;
import com.peak.main.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemControl {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(itemService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable int id) {
        return ResponseEntity.ok(new Response(itemService.findById(id)));
    }
}
