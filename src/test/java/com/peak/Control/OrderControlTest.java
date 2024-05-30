package com.peak.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.model.Order;
import com.peak.main.model.OrderDetails;
import com.peak.main.request.Response;
import com.peak.main.service.OrderService;
import com.peak.util.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    private final List<Order> orders = new ArrayList<>(List.of(
            new Order(1L, 1L, LocalDateTime.now(), 100, new ArrayList<>()),
            new Order(2L, 1L, LocalDateTime.now(), 200, new ArrayList<>()),
            new Order(3L, 2L, LocalDateTime.now(), 300, new ArrayList<>())
    ));

    private final OrderDetails orderDetails = OrderDetails.builder()
            .id(1L)
            .name("name")
            .orderID(1L)
            .storeID(1L)
            .itemID(1L)
            .quantity(1)
            .cost(100)
            .image("image.png")
            .status(Status.ORDER)
            .type("type")
            .build();

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = { "ADMIN" })
    void testAdminGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(orders);
        String expectedJson = objectMapper.writeValueAsString(new Response(orders));
        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson));
    }


    @Test
    @WithUserDetails
    @WithMockUser(authorities = { "USER" })
    void testUserGetOrders() throws Exception {
        List<Order> expectedOrders = List.of(orders.get(0), orders.get(1));
        when(orderService.getOrdersByUserId(anyLong())).thenReturn(expectedOrders);
        String expectedJson = objectMapper.writeValueAsString(new Response(expectedOrders));
        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson));
        verify(orderService).getOrdersByUserId(anyLong());
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testUpdateOrderDetailStatus_Seller_Success() throws Exception {
        long orderDetailId = 1L;
        Status newStatus = Status.SHIPPING;

        OrderDetails updatedOrderDetail = orderDetails;
        updatedOrderDetail.setStatus(newStatus);
        when(orderService.isOrderDetailOwnedBySeller(any(long.class), any(long.class))).thenReturn(true);
        when(orderService.updateOrderDetailStatus(any(long.class), any(Status.class))).thenReturn(updatedOrderDetail);

        String expectedJson = objectMapper.writeValueAsString(new Response(updatedOrderDetail));
        mockMvc.perform(put("/order/{orderDetailId}", orderDetailId)
                        .param("status", newStatus.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson));
        verify(orderService).isOrderDetailOwnedBySeller(any(long.class), any(long.class));
        verify(orderService).updateOrderDetailStatus(any(long.class), any(Status.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testUpdateOrderDetailStatus_Seller_Unauthorized() throws Exception {
        long orderDetailId = 1L;
        Status newStatus = Status.SHIPPING;
        when(orderService.isOrderDetailOwnedBySeller(any(long.class), any(long.class))).thenReturn(false);
        mockMvc.perform(put("/order/{orderDetailId}", orderDetailId)
                        .param("status", newStatus.name()))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.body").value("You are not authorized to update this order detail."));
    }

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = { "ADMIN" })
    void testUpdateOrderDetailStatus_Admin_Success() throws Exception {
        Status newStatus = Status.SHIPPING;

        OrderDetails updatedOrderDetail = orderDetails;
        updatedOrderDetail.setStatus(newStatus);

        when(orderService.updateOrderDetailStatus(updatedOrderDetail.getId(), newStatus)).thenReturn(updatedOrderDetail);
        String expectedJson = objectMapper.writeValueAsString(new Response(updatedOrderDetail));
        mockMvc.perform(put("/order/{orderDetailId}", updatedOrderDetail.getId())
                        .param("status", newStatus.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson));
        verify(orderService).updateOrderDetailStatus(updatedOrderDetail.getId(), newStatus);
    }

}

