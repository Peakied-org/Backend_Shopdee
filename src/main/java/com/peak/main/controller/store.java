package com.peak.main.controller;

import com.peak.Util.Role;
import com.peak.main.Request.RequestItem;
import com.peak.main.Request.Response;
import com.peak.main.model.Store;
import com.peak.main.model.User;
import com.peak.main.service.StoreService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@AllArgsConstructor
public class store {

    private final StoreService storeService;

//  /store
    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(storeService.findAll()));
    }

//  /store
    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@PathVariable long id) {
        return ResponseEntity.ok(new Response(storeService.findById(id)));
    }

/*  /store
    {
        "name":"name",
        "detail":"detail",
        "image":"http"
    }
 */
    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<Response> add(@RequestBody Store store, Authentication authentication) {
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

//  /store/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> delete(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermitionStore(user.getId(), id)) {
            storeService.deleteById(id);
            return ResponseEntity.ok(new Response("[]"));
        }

        return ResponseEntity.status(403).build();
    }

/*  /store/{id}
    {
        "name":"name",
        "cost":"12",
        "category":"big",
        "detail":"detail",
        "stock":"1",            // could be null
        "sold":"1",             // could be null
        "discount":"1"          // could be null
        "types":["red","blue"], // could be null
        "images":["image1"]     // could be null
    }
 */
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
        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermitionStore(user.getId(), id)) return ResponseEntity.status(201).body(new Response(storeService.saveToStore(item)));

        return ResponseEntity.status(403).build();
    }

//  /store/{storeId}/item/{itemId}
    @PutMapping("/item/{iid}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> updateItem(@PathVariable Long iid, Authentication authentication, @RequestBody RequestItem newItem) {
        User user = (User) authentication.getPrincipal();

        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermitionItem(user.getId(), iid)) {
            return ResponseEntity.ok(new Response(storeService.updateItem(newItem, iid)));
        }
        return ResponseEntity.status(403).build();
    }

//  /store/item/{itemId}
    @DeleteMapping("/item/{iid}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Response> deleteItem(@PathVariable Long iid, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        if (user.getRole().equals(Role.ADMIN) || storeService.hasPermitionItem(user.getId(), iid)) {
            storeService.deleteFromStore(iid);
            return ResponseEntity.ok(new Response("[]"));
        }
        return ResponseEntity.status(403).build();
    }

}
