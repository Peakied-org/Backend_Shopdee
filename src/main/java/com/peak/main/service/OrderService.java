package com.peak.main.service;

import com.peak.util.Status;
import com.peak.main.model.*;
import com.peak.main.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderDetailsRepository orderDetailsRepository;

    private final StoreRepository storeRepository;

    private final CartRepository cartRepository;

    // For ADMIN
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // For SELLER
    public List<OrderDetails> getOrderDetailsBySeller(User seller) {
        Store store = storeRepository.findByUserID(seller.getId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found for user"));
        return orderDetailsRepository.findByStoreID(store.getId());
    }

    // For USER
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserID(userId);
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {
        Cart cart = cartRepository.findFirstByUserID(userId);
        if (cart == null || cart.getCartDetails().isEmpty()) {
            throw new EntityNotFoundException("Cart is empty or not found for user ID: " + userId);
        }

        Order newOrder = new Order();
        newOrder.setUserID(userId);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setTotalCost(cart.getTotalCost());
        Order savedOrder = orderRepository.save(newOrder);

        for (CartDetails cartDetail : cart.getCartDetails()) {
            OrderDetails orderDetail = getOrderDetails(cartDetail, savedOrder);
            orderDetailsRepository.save(orderDetail);
        }
        List<OrderDetails> savedOrderDetails = orderDetailsRepository.findByOrderID(savedOrder.getId());
        cartRepository.delete(cart);
        savedOrder.setOrderDetails(savedOrderDetails);
        return savedOrder;
    }

    private static OrderDetails getOrderDetails(CartDetails cartDetail, Order savedOrder) {
        OrderDetails orderDetail = new OrderDetails();
        orderDetail.setName(cartDetail.getName());
        orderDetail.setQuantity(cartDetail.getQuantity());
        orderDetail.setCost(cartDetail.getCost());
        orderDetail.setImage(cartDetail.getImage());
        orderDetail.setOrderID(savedOrder.getId());
        orderDetail.setStoreID(cartDetail.getStoreID());
        orderDetail.setItemID(cartDetail.getItemID());
        orderDetail.setStatus(Status.ORDER);
        orderDetail.setType(cartDetail.getType());
        return orderDetail;
    }

    @Transactional
    public OrderDetails updateOrderDetailStatus(Long orderDetailId, Status newStatus) {
        OrderDetails orderDetail = orderDetailsRepository.findById(orderDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Order detail not found with ID: " + orderDetailId));
        orderDetail.setStatus(newStatus);
        return orderDetailsRepository.save(orderDetail);
    }

    public boolean isOrderDetailOwnedBySeller(Long orderDetailId, Long sellerId) {
        Store store = storeRepository.findByUserID(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found for user"));
        return orderDetailsRepository.existsByIdAndStoreID(orderDetailId, store.getId());
    }
}
