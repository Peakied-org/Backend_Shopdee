package com.peak.main.service;

import com.peak.main.model.Item;
import com.peak.main.repository.ItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item save(Item item) {
        if (item.getStock() == null) item.setStock(0);
        if (item.getDiscount() == null) item.setDiscount(0);
        if (item.getSold() == null) item.setSold(0);

        return itemRepository.save(item);
    }

    public void deleteById(long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> findByName(String name) {
        return itemRepository.findByName(name);
    }
}
