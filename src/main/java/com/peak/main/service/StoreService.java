package com.peak.main.service;

import com.peak.main.Request.RequestItem;
import com.peak.main.model.Item;
import com.peak.main.model.Store;
import com.peak.main.repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final ItemService itemService;

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store save(Store store) {
        storeRepository.save(store);
        store.setItems(new ArrayList<>());
        return store;
    }

    public Optional<Store> findByUserId(long id) {
        return storeRepository.findByUserID(id);
    }

    public Optional<Store> findById(long id) {
        return storeRepository.findById(id);
    }

    public void deleteById(long id) {
        storeRepository.deleteById(id);
    }

    public boolean hasPermitionStore(long userID, long storeID) {
        Optional<Store> store = storeRepository.findByUserID(userID);
        if (store.isEmpty()) return false;
        return store.get().getId().equals(storeID);
    }

    public boolean hasPermitionItem(long storeID, long itemID) {
        Optional<Item> item = itemService.findById(itemID);
        if (item.isEmpty()) return false;
        return item.get().getStoreID().equals(storeID);
    }

    public Item saveToStore(RequestItem item) {
        return itemService.save(item);
    }

    public void deleteFromStore(long id) {
        itemService.deleteById(id);
    }

    public Item updateItem(RequestItem newItem, long id) {
        return itemService.updateItem(newItem, id);
    }
}
