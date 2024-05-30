package com.peak.Service;

import com.peak.main.model.Coupon;
import com.peak.main.repository.CouponRepository;
import com.peak.main.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        assertEquals("Coupon 1", result.get(0).getName());
        assertEquals(10, result.get(0).getDiscount());
        assertEquals("image1.jpg", result.get(0).getImage());

        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_NoCoupons() {
        when(couponRepository.findAll()).thenReturn(Collections.emptyList());
        List<Coupon> result = couponService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testSave() {

        Coupon coupon = new Coupon(1L, "Coupon 1", 10, "image1.jpg");

        when(couponRepository.save(coupon)).thenReturn(coupon);
        Coupon result = couponService.save(coupon);
        assertEquals(coupon.getId(), result.getId());
        assertEquals(coupon.getName(), result.getName());
        assertEquals(coupon.getDiscount(), result.getDiscount());
        assertEquals(coupon.getImage(), result.getImage());

        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;
        couponService.deleteById(id);
        verify(couponRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteById_CouponNotFound() {
        Long id = 1L;
        doThrow(new EntityNotFoundException("Coupon not found")).when(couponRepository).deleteById(id);
        assertThrows(EntityNotFoundException.class, () -> couponService.deleteById(id));
        verify(couponRepository, times(1)).deleteById(id);
    }
}