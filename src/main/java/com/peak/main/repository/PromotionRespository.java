package com.peak.main.repository;

import com.peak.main.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRespository extends JpaRepository<Promotion, Long> {
}
