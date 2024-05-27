package com.peak;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.peak.main.controller.CouponControl;
import com.peak.main.service.CouponService;
import com.peak.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CouponControl.class)
class CouponControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testGetCoupon() throws Exception {
        // Mock JWT token
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxNjgwMDUxMCwiZXhwIjoxNzE2ODE0OTEwfQ.brE0NvCX0_J5fZ2hx0LNzRMYfpc5fdPMYfDj-aESNWk";

        // Mock the behavior of jwtService to return the valid token
        Mockito.when(jwtService.extractUsername(jwtToken)).thenReturn("admin");

        mockMvc.perform(get("/coupons")
                        .header("Authorization", "Bearer " + jwtToken)) // Include the JWT token in the request
                .andExpect(status().isOk())
                .andExpect(content().string("Expected Response"));
    }
}
