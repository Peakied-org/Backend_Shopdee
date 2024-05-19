package com.peak.main.controller;

import com.peak.Util.Role;
import com.peak.main.Request.RequestItem;
import com.peak.main.Request.RequestName;
import com.peak.main.model.Item;
import com.peak.main.Request.Response;
import com.peak.main.model.User;
import com.peak.main.repository.ItemRepository;
import com.peak.main.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
