package com.peak.main.service;

import com.peak.main.model.Promotion;
import com.peak.main.repository.PromotionRespository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PromotionService {

    private final PromotionRespository promotionRespository;

    public List<Promotion> findAll() {
        return promotionRespository.findAll();
    }

    public Promotion save(Promotion promotion) {
        return promotionRespository.save(promotion);
    }

    public void deleteById(long id) {
        promotionRespository.deleteById(id);
    }
}
