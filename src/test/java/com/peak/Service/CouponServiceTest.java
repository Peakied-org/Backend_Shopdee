package com.peak.Service;

import com.peak.main.model.Coupon;
import com.peak.main.repository.CouponRepository;
import com.peak.main.service.CouponService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void testFindAll() {

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(1L, "Coupon 1", 10, "image1.jpg"));
        coupons.add(new Coupon(2L, "Coupon 2", 15, "image2.jpg"));

        when(couponRepository.findAll()).thenReturn(coupons);
        List<Coupon> result = couponService.findAll();

        assertEquals(2, result.size());

        // call 1 time
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testSave() {

        Coupon coupon = new Coupon(1L, "Coupon 1", 10, "image1.jpg");

        when(couponRepository.save(coupon)).thenReturn(coupon);
        Coupon result = couponService.save(coupon);

        assertEquals("Coupon 1", result.getName());

        // call 1 time
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testDeleteById() {

        Long id = 1L;
        couponService.deleteById(id);

        // call 1 time
        verify(couponRepository, times(1)).deleteById(id);
    }

}