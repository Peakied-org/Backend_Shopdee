package com.peak.main.controller;

import com.peak.main.model.Coupon;
import com.peak.main.Request.Response;
import com.peak.main.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@AllArgsConstructor
public class coupon {

    private final CouponService couponService;

//  /coupon
    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(couponService.findAll()));
    }

/*  /coupon
    {
        "name":"name",
        "discount":"1",
        "image":"image"
    }
 */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> add(@RequestBody Coupon coupon) {
        if (coupon.getName() == null ||
                coupon.getDiscount() == null ||
                coupon.getImage() == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.status(201).body(new Response(couponService.save(coupon)));
    }

//  /coupon/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        couponService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
