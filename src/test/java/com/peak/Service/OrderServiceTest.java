package com.peak.Service;

import com.peak.main.model.*;
import com.peak.main.repository.CartRepository;
import com.peak.main.repository.OrderDetailsRepository;
import com.peak.main.repository.OrderRepository;
import com.peak.main.service.OrderService;
import com.peak.util.Status;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailsRepository orderDetailsRepository;
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrderFromCart_Success() {
        Long userId = 1L;
        Cart cart = Cart.builder().id(1L).userID(userId).totalCost(200).build();
        cart.setCartDetails(List.of(
                CartDetails.builder().id(1L).name("Item 1").quantity(2).cost(50).build(),
                CartDetails.builder().id(2L).name("Item 2").quantity(1).cost(100).build()
        ));

        Order expectedOrder = Order.builder().userID(userId).totalCost(200).build();

        when(cartRepository.findFirstByUserID(userId)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order order = i.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(orderDetailsRepository.findByOrderID(anyLong())).thenReturn(List.of(
                OrderDetails.builder().id(1L).name("Item 1").quantity(2).cost(50).build(),
                OrderDetails.builder().id(2L).name("Item 2").quantity(1).cost(100).build()
        ));

        Order result = orderService.createOrderFromCart(userId);

        assertEquals(expectedOrder.getUserID(), result.getUserID());
        assertEquals(expectedOrder.getTotalCost(), result.getTotalCost());
        assertEquals(2, result.getOrderDetails().size());
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    void testGetOrderDetails() {
        CartDetails cartDetail = CartDetails.builder().id(1L).name("Test Product").quantity(3).cost(25).image("product_image.jpg").storeID(10L).itemID(101L).type("Regular").build();
        Order savedOrder = Order.builder().id(20L).build();

        OrderDetails result = OrderService.getOrderDetails(cartDetail, savedOrder);
        assertAll(
                () -> assertEquals("Test Product", result.getName()),
                () -> assertEquals(3, result.getQuantity()),
                () -> assertEquals(25, result.getCost()),
                () -> assertEquals("product_image.jpg", result.getImage()),
                () -> assertEquals(20L, result.getOrderID()),
                () -> assertEquals(10L, result.getStoreID()),
                () -> assertEquals(101L, result.getItemID()),
                () -> assertEquals(Status.ORDER, result.getStatus()),
                () -> assertEquals("Regular", result.getType())
        );
    }

    @Test
    void testUpdateOrderDetailStatus_Success() {
        Long orderDetailId = 1L;
        Status newStatus = Status.SHIPPING;
        OrderDetails existingOrderDetail = OrderDetails.builder().id(orderDetailId).status(Status.ORDER).build();
        when(orderDetailsRepository.findById(orderDetailId)).thenReturn(Optional.of(existingOrderDetail));
        when(orderDetailsRepository.save(any(OrderDetails.class))).thenAnswer(i -> i.getArgument(0));
        OrderDetails updatedOrderDetail = orderService.updateOrderDetailStatus(orderDetailId, newStatus);
        assertEquals(newStatus, updatedOrderDetail.getStatus());
    }

    @Test
    void testUpdateOrderDetailStatus_OrderDetailNotFound() {
        Long orderDetailId = 2L;
        Status newStatus = Status.SHIPPING;
        when(orderDetailsRepository.findById(orderDetailId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrderDetailStatus(orderDetailId, newStatus));
    }

}

