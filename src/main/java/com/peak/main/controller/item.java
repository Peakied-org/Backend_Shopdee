package com.peak.main.controller;

import com.peak.main.Request.Response;
import com.peak.main.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class item {

    private final ItemService itemService;

//  /item
    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(itemService.findAll()));
    }
}
