package com.peak.main.repository;

import com.peak.main.model.CartDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailsRepository extends JpaRepository<CartDetails, Long> {
    CartDetails findByCartIDAndItemIDAndType(Long cartId, Long itemId, String type);
}

