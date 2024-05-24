package com.peak.main.controller;

import com.peak.main.request.Response;
import com.peak.main.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@AllArgsConstructor
public class Coupon {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(couponService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> add(@RequestBody com.peak.main.model.Coupon coupon) {
        if (coupon.getName() == null ||
                coupon.getDiscount() == null ||
                coupon.getImage() == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.status(201).body(new Response(couponService.save(coupon)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        couponService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
