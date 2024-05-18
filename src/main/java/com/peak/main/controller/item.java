package com.peak.main.controller;

import com.peak.main.Request.RequestName;
import com.peak.main.model.Item;
import com.peak.main.Request.Response;
import com.peak.main.repository.ItemRepository;
import com.peak.main.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

//  /item/search
    @GetMapping("/search")
    public ResponseEntity<Response> getById(RequestName name) {
        return ResponseEntity.ok(new Response(itemService.findByName(name.getName())));
    }

/*  /item
    {
        "name":"name",
        "cost":"12",
        "storeID":"1",
        "category":"big",
        "detail":"detail"
        "stock":"1",        // could be null
        "sold":"1",         // could be null
        "discount":"1"      // could be null
    }
 */
    @PostMapping
    public ResponseEntity<Response> add(@RequestBody Item item) {
        if (item.getName() == null ||
                item.getCost() == null ||
                item.getStoreID() == null ||
                item.getCategory() == null ||
                item.getDetail() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new Response(itemService.save(item)));
    }

    // /item/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable long id) {
        itemService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
