package com.peak.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.model.Promotion;
import com.peak.main.service.PromotionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class PromotionControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PromotionService promotionService;

    private ArrayList<Promotion> promotions = new ArrayList<>(List.of(
            new Promotion(1L, "image1"),
            new Promotion(2L, "image2"),
            new Promotion(3L, "image3")
    ));

    @Test
    void TestGetPromotion() throws Exception {

        when(promotionService.findAll()).thenReturn(promotions);

        mockMvc.perform(get("/promotion"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body[0].image").value("image1"),
                        jsonPath("$.body[1].image").value("image2"),
                        jsonPath("$.body[2].image").value("image3")
                );
        verify(promotionService, times(1)).findAll();
    }

    @Test
    @WithMockUser(authorities = "USER")
    void TestAddPromotionByUser() throws Exception {

        Promotion promotion = new Promotion(4L, "image4");

        String json = objectMapper.writeValueAsString(promotion);

        mockMvc.perform(post("/promotion")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(promotionService, times(0)).save(promotion);
    }

    @Test
    @WithMockUser(authorities = "SELLER")
    void TestAddPromotionBySeller() throws Exception {

        Promotion promotion = new Promotion(4L, "image4");

        String json = objectMapper.writeValueAsString(promotion);

        when(promotionService.save(any(Promotion.class))).then(invocationOnMock -> {
            promotions.add(invocationOnMock.getArgument(0));
            return promotion;
        });

        mockMvc.perform(post("/promotion")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(promotionService, times(0)).save(promotion);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void TestAddPromotion() throws Exception {

        Promotion promotion = new Promotion(4L, "image4");

        String json = objectMapper.writeValueAsString(promotion);

        when(promotionService.save(any(Promotion.class))).then(invocationOnMock -> {
            promotions.add(invocationOnMock.getArgument(0));
            return promotion;
        });

        mockMvc.perform(post("/promotion")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.body.image").value("image4")
                );
        verify(promotionService, times(1)).save(any(Promotion.class));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void TestDeletePromotionByUser() throws Exception {

        doNothing().when(promotionService).deleteById(any(long.class));

        mockMvc.perform(delete("/promotion/1"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(promotionService, times(0)).deleteById(any(long.class));
    }

    @Test
    @WithMockUser(authorities = "SELLER")
    void TestDeletePromotionBySeller() throws Exception {

        doNothing().when(promotionService).deleteById(any(long.class));

        mockMvc.perform(delete("/promotion/1"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(promotionService, times(0)).deleteById(any(long.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void TestDeletePromotionByAdmin() throws Exception {

        doNothing().when(promotionService).deleteById(any(long.class));

        mockMvc.perform(delete("/promotion/1"))
                .andExpectAll(
                        status().isOk()
                );
        verify(promotionService, times(1)).deleteById(any(long.class));
    }



}
