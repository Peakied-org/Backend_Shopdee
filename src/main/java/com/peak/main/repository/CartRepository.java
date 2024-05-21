package com.peak.main.repository;

import com.peak.main.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserID(Long userID);
    Cart findFirstByUserID(Long userId);
}
