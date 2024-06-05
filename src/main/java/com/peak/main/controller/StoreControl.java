package com.peak.main.controller;

import com.peak.main.model.Store;
import com.peak.util.Role;
import com.peak.main.request.RequestItem;
import com.peak.main.request.Response;
import com.peak.main.model.User;
import com.peak.main.service.StoreService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/store")
@AllArgsConstructor
public class StoreControl {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(storeService.findAll()));
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<Response> getByUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new Response(storeService.findByUserId(user.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@PathVariable long id) {
        return ResponseEntity.ok(new Response(storeService.findById(id)));
    }

    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Response> addStore(@RequestBody Store store, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (storeService.findByUserId(user.getId()).isPresent()) return ResponseEntity.ok(new Response("Already exists"));
        if (store.getName() == null ||
                store.getDetail() == null ||
                store.getImage() == null ||
                store.getBanner() == null)
            return ResponseEntity.badRequest().build();
        store.setUserID(user.getId());

        return ResponseEntity.status(201).body(new Response(storeService.save(store)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Response> updateStore(@PathVariable long id, @RequestBody Store newStore, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Optional<Store> findStore = storeService.findById(id);
        if (findStore.isEmpty()) return ResponseEntity.badRequest().body(new Response("Dont have store"));

        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermissionStore(user.getId(), id)) {
            return ResponseEntity.status(200).body(new Response(storeService.update(findStore.get(), newStore)));
        }

        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> delete(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermissionStore(user.getId(), id)) {
            storeService.deleteById(id);
            return ResponseEntity.ok(new Response("[]"));
        }

        return ResponseEntity.status(403).build();
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> addItem(@PathVariable Long id, @RequestBody RequestItem item, Authentication authentication) {
        if (item.getName() == null ||
                item.getCost() == null ||
                item.getCategory() == null ||
                item.getDetail() == null)
            return ResponseEntity.badRequest().build();

        item.setStoreID(id);

        if (storeService.findById(id).isEmpty()) return ResponseEntity.badRequest().build();
        User user = (User) authentication.getPrincipal();
        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermissionStore(user.getId(), id)) return ResponseEntity.status(201).body(new Response(storeService.saveToStore(item)));

        return ResponseEntity.status(403).build();
    }

    @PutMapping("/item/{iid}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> updateItem(@PathVariable Long iid, Authentication authentication, @RequestBody RequestItem newItem) {
        User user = (User) authentication.getPrincipal();

        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermissionItem(user.getId(), iid)) {
            return ResponseEntity.ok(new Response(storeService.updateItem(newItem, iid)));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/item/{iid}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> deleteItem(@PathVariable Long iid, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermissionItem(user.getId(), iid)) {
            storeService.deleteFromStore(iid);
            return ResponseEntity.ok(new Response("[]"));
        }
        return ResponseEntity.status(403).build();
    }

}
