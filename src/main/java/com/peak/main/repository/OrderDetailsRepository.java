package com.peak.main.repository;

import com.peak.main.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findByStoreID(Long storeId);
    List<OrderDetails> findByOrderID(Long id);
    boolean existsByIdAndStoreID(Long orderDetailId, Long sellerId);
}
