package com.peak.main.controller;

import com.peak.main.model.Coupon;
import com.peak.main.Request.Response;
import com.peak.main.repository.CouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@AllArgsConstructor
public class coupon {

    private final CouponRepository couponRepository;

    // /coupon
    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(couponRepository.findAll()));
    }

    // /coupon
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> add(@RequestBody Coupon coupon) {
        if (coupon.getName() == null ||
                coupon.getDiscount() == null ||

                coupon.getImage() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new Response(couponRepository.save(coupon)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
