package com.peak.main.service;

import com.peak.main.model.Cart;
import com.peak.main.model.CartDetails;
import com.peak.main.model.Item;
import com.peak.main.repository.CartRepository;
import com.peak.main.repository.CartDetailsRepository;
import com.peak.main.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartDetailsRepository cartDetailsRepository;

    private final ItemRepository itemRepository;

    public Cart getCartsByUserId(Long userId) {
        return cartRepository.findByUserID(userId);
    }

    @Transactional
    public Cart addToCart(Long userId, Long itemId, Integer quantity, String type) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + itemId));
        boolean hasMultipleTypes = !item.getType().isEmpty();
        if (hasMultipleTypes && (type == null || type.isEmpty())) {
            throw new IllegalArgumentException("This item has multiple types. Please specify a type.");
        }
        Cart existingCart = cartRepository.findFirstByUserID(userId);
        if (existingCart == null) {
            existingCart = new Cart();
            existingCart.setUserID(userId);
            existingCart.setTotalCost(0);
            existingCart.setCartDetails(new ArrayList<>());
            existingCart = cartRepository.save(existingCart);
        }

        CartDetails existingCartDetails = cartDetailsRepository.findByCartIDAndItemIDAndType(existingCart.getId(), itemId, type);
        if (existingCartDetails == null && type != null && !item.getType().contains(type)) {
            throw new IllegalArgumentException("Invalid type for item: " + itemId);
        }
        if (existingCartDetails == null) {
            CartDetails newCartDetails = new CartDetails();
            newCartDetails.setName(item.getName());
            newCartDetails.setQuantity(quantity);
            newCartDetails.setCost(item.getCost());
            newCartDetails.setDiscount(item.getDiscount());
            newCartDetails.setImage(item.getImage() != null && !item.getImage().isEmpty() ? item.getImage().get(0) : null);
            newCartDetails.setCartID(existingCart.getId());
            newCartDetails.setItemID(itemId);
            newCartDetails.setStoreID(item.getStoreID());
            newCartDetails.setType(type);
            cartDetailsRepository.save(newCartDetails);
            existingCart.getCartDetails().add(newCartDetails);
        } else {
            existingCartDetails.setQuantity(existingCartDetails.getQuantity() + quantity);
            cartDetailsRepository.save(existingCartDetails);
        }

        updateTotalCost(existingCart);
        return cartRepository.save(existingCart);
    }

    @Transactional
    public Cart updateCart(Long userId, Long cartDetailsId, Integer newQuantity) {
        Cart existingCart = cartRepository.findFirstByUserID(userId);
        if (existingCart == null) {
            throw new EntityNotFoundException("Cart not found for user with ID: " + userId);
        }
        CartDetails cartDetailsToUpdate = existingCart.getCartDetails().stream()
                .filter(details -> details.getId().equals(cartDetailsId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart details not found with ID: " + cartDetailsId));
        cartDetailsToUpdate.setQuantity(newQuantity);
        cartDetailsRepository.save(cartDetailsToUpdate);
        updateTotalCost(existingCart);
        return cartRepository.save(existingCart);
    }


    @Transactional
    public void deleteFromCart(Long userId, Long cartDetailsId) {
        Cart existingCart = cartRepository.findFirstByUserID(userId);
        if (existingCart == null) {
            throw new EntityNotFoundException("Cart not found for user with ID: " + userId);
        }
        CartDetails cartDetailsToDelete = existingCart.getCartDetails().stream()
                .filter(details -> details.getId().equals(cartDetailsId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart details not found with ID: " + cartDetailsId));
        existingCart.getCartDetails().remove(cartDetailsToDelete);
        cartDetailsRepository.delete(cartDetailsToDelete);

        updateTotalCost(existingCart);
        cartRepository.save(existingCart);
    }

    private void updateTotalCost(Cart existingCart) {
        Integer totalCost = existingCart.getCartDetails().stream()
                .mapToInt(cd -> cd.getCost() * cd.getQuantity())
                .sum();
        existingCart.setTotalCost(totalCost);
    }
}
