package com.peak.Service;

import com.peak.main.model.Promotion;
import com.peak.main.repository.PromotionRepository;
import com.peak.main.service.PromotionService;
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
public class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionService promotionService;

    private final ArrayList<Promotion> promotions = new ArrayList<>(List.of(
            new Promotion(1L, "image1"),
            new Promotion(2L, "image2"),
            new Promotion(3L, "image3")
    ));

    @Test
    void TestGetAllPromotions() {

        when(promotionRepository.findAll()).thenReturn(promotions);

        assertEquals(promotions, promotionService.findAll());

        verify(promotionRepository, times(1)).findAll();
    }

    @Test
    void TestSavePromotion() {

        Promotion promotion = new Promotion(1L, "image1");

        when(promotionRepository.save(promotion)).then(invocationOnMock -> {
            promotions.add(invocationOnMock.getArgument(0));
            return promotion;
        });

        assertEquals(promotion, promotionService.save(promotion));

        verify(promotionRepository, times(1)).save(promotion);
    }

    @Test
    void TestDeletePromotion() {

        doNothing().when(promotionRepository).deleteById(any(long.class));

        promotionService.deleteById(1L);

        verify(promotionRepository, times(1)).deleteById(anyLong());
    }










}
