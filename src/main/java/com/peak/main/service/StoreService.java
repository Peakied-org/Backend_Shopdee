package com.peak.main.service;

import com.peak.main.model.Store;
import com.peak.main.repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store save(Store store) {
        return storeRepository.save(store);
    }

    public Optional<Store> findById(long id) {
        return storeRepository.findById(id);
    }

    public void deleteById(long id) {
        storeRepository.deleteById(id);
    }
}
