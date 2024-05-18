package com.peak.main.service;

import com.peak.main.model.Coupon;
import com.peak.main.repository.CouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteById(Long id) {
        couponRepository.deleteById(id);
    }
}
