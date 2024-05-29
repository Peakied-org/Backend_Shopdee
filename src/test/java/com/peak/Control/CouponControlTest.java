package com.peak.Control;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.model.Coupon;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import com.peak.main.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class CouponControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetCoupon() throws Exception {

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(1L,"name",12,"image1.png"));
        coupons.add(new Coupon(2L,"name1",13,"image2.png"));
        coupons.add(new Coupon(3L,"name3",14,"image3.png"));

        when(couponService.findAll()).thenReturn(coupons);

        mockMvc.perform(get("/coupon"))
                .andExpectAll(
                    status().isOk(),
                    content().contentType("application/json"),
                    jsonPath("$.body").isArray(),
                    jsonPath("$.body[0].id").value(1),
                    jsonPath("$.body[0].name").value("name"),
                    jsonPath("$.body[0].discount").value(12),
                    jsonPath("$.body[0].image").value("image1.png"),
                    jsonPath("$.body[1].id").value(2),
                    jsonPath("$.body[1].name").value("name1"),
                    jsonPath("$.body[1].discount").value(13),
                    jsonPath("$.body[1].image").value("image2.png"),
                    jsonPath("$.body[2].id").value(3),
                    jsonPath("$.body[2].name").value("name3"),
                    jsonPath("$.body[2].discount").value(14),
                    jsonPath("$.body[2].image").value("image3.png")
                );
    }

    @Test
    @WithMockUser(authorities = { "ADMIN" })
    void testCreateCouponAsAdmin() throws Exception {
        Coupon coupon = new Coupon(1L, "name", 12, "image1.png");

        when(couponService.save(any(Coupon.class))).thenReturn(coupon);

        String couponJson = objectMapper.writeValueAsString(coupon);

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(couponJson))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.body.name").value("name"), 
                        jsonPath("$.body.discount").value(12), 
                        jsonPath("$.body.image").value("image1.png")
                );
    }

    @Test
    @WithMockUser(authorities = { "USER" })
    void testCreateCouponAsUser() throws Exception {
        Coupon coupon = new Coupon(1L, "name", 12, "image1.png");

        String couponJson = objectMapper.writeValueAsString(coupon);

        mockMvc.perform(post("/coupon")
                        .contentType("application/json")
                        .content(couponJson))
                .andExpect(status().isForbidden());
    }
}
