package com.peak.Service;

import com.peak.main.model.Cart;
import com.peak.main.model.CartDetails;
import com.peak.main.model.Item;
import com.peak.main.model.Type;
import com.peak.main.repository.CartRepository;
import com.peak.main.repository.CartDetailsRepository;
import com.peak.main.repository.ItemRepository;
import com.peak.main.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailsRepository cartDetailsRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartService cartService;

    private final List<Cart> carts = new ArrayList<>(List.of(
            new Cart(1L, 11L, 0, new ArrayList<>()),
            new Cart(2L, 12L, 0, new ArrayList<>()),
            new Cart(3L, 13L, 0, new ArrayList<>())
    ));

    @Test
    void testGetCartsByUserId() {
        when(cartRepository.findByUserID(11L)).thenReturn(carts.get(0));

        assertEquals(carts.get(0), cartService.getCartsByUserId(11L));

        verify(cartRepository, times(1)).findByUserID(11L);
    }

    @Test
    void testAddToCart_MultipleTypesWithoutType() {
        Long userId = 4L;
        Long itemId = 104L;
        int quantity = 1;

        Item itemWithTypes = new Item(itemId, "Multi-Type Item", 15L, 0, 60, "Category", "Detail", 10, 0,
                List.of(new Type(1L, itemId, "Small"), new Type(2L, itemId, "Large")), new ArrayList<>());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithTypes));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(userId, itemId, quantity, null));

        assertEquals("This item has multiple types. Please specify a type.", exception.getMessage());
    }

    @Test
    void testAddToCart_InvalidType() {
        Long userId = 5L;
        Long itemId = 105L;
        int quantity = 1;
        Item itemWithTypes = new Item(itemId, "Multi-Type Item", 16L, 0, 60, "Category", "Detail", 10, 0,
                List.of(new Type(3L, itemId, "Small"), new Type(4L, itemId, "Medium")), new ArrayList<>());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWithTypes));
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        when(cartRepository.findFirstByUserID(userId)).thenReturn(emptyCart);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(userId, itemId, quantity, "Large"));
        assertEquals("Invalid type for item: " + itemId, exception.getMessage());
    }

    @Test
    void testAddToCart_NewCart() {
        Long userId = 6L;
        Long itemId = 106L;
        int quantity = 2;
        String type = "Small";
        Item item = Item.builder().id(itemId).name("Test Item").cost(50).types(List.of(Type.builder().itemID(itemId).type(type).build())).storeID(17L).images(new ArrayList<>()).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartRepository.findFirstByUserID(userId)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);
        when(cartDetailsRepository.save(any(CartDetails.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.addToCart(userId, itemId, quantity, type);
        assertEquals(userId, result.getUserID());
        assertEquals(100, result.getTotalCost());
        assertEquals(1, result.getCartDetails().size());
        CartDetails details = result.getCartDetails().get(0);
        assertEquals("Test Item", details.getName());
        assertEquals(quantity, details.getQuantity());
    }

    @Test
    void testAddToCart_ItemNotFound() {
        Long userId = 7L;
        Long itemId = 999L;
        int quantity = 1;
        String type = "Small";
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> cartService.addToCart(userId, itemId, quantity, type));
        assertEquals("Item not found with ID: " + itemId, exception.getMessage());
    }

    @Test
    void testAddToCart_ExistingCart() {
        Long userId = 8L;
        Long itemId = 107L;
        int quantityToAdd = 3;
        Cart existingCart = Cart.builder().id(1L).userID(userId).totalCost(80).cartDetails(new ArrayList<>(List.of(CartDetails.builder().id(1L).cartID(1L).storeID(12L).itemID(108L).name("Existing Item").quantity(2).cost(40).discount(0).build()))).build();
        Item itemToAdd = Item.builder().id(itemId).name("New Item").cost(30).types(List.of(Type.builder().itemID(itemId).type("Medium").build())).storeID(18L).images(new ArrayList<>()).discount(0).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemToAdd));
        when(cartRepository.findFirstByUserID(userId)).thenReturn(existingCart);
        when(cartDetailsRepository.save(any(CartDetails.class))).thenAnswer(i -> i.getArguments()[0]);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.addToCart(userId, itemId, quantityToAdd, "Medium");

        assertEquals(2, result.getCartDetails().size());
        assertEquals(170, result.getTotalCost());
        CartDetails newDetails = result.getCartDetails().get(1);
        assertAll(
                () -> assertEquals("New Item", newDetails.getName()),
                () -> assertEquals(quantityToAdd, newDetails.getQuantity()),
                () -> assertEquals(30, newDetails.getCost()),
                () -> assertEquals(0, newDetails.getDiscount()),
                () -> assertEquals(existingCart.getId(), newDetails.getCartID()),
                () -> assertEquals(18L, newDetails.getStoreID()),
                () -> assertEquals("Medium", newDetails.getType())
        );
    }

    @Test
    void testUpdateCart_Success() {
        Long userId = 10L;
        Long cartDetailsId = 5L;
        int newQuantity = 5;

        Cart existingCart = Cart.builder().id(5L).userID(userId).totalCost(120)
                .cartDetails(new ArrayList<>(List.of(
                        CartDetails.builder().id(cartDetailsId).cartID(5L).storeID(20L)
                                .itemID(110L).name("Update Item").quantity(3).cost(40).discount(0).build()
                ))).build();

        when(cartRepository.findFirstByUserID(userId)).thenReturn(existingCart);
        when(cartDetailsRepository.save(any(CartDetails.class))).thenAnswer(i -> i.getArguments()[0]);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart updatedCart = cartService.updateCart(userId, cartDetailsId, newQuantity);

        assertEquals(200, updatedCart.getTotalCost());
        assertEquals(newQuantity, updatedCart.getCartDetails().get(0).getQuantity());
    }


    @Test
    void testUpdateCart_CartNotFound() {
        Long userId = 11L;
        Long cartDetailsId = 6L;
        int newQuantity = 5;
        when(cartRepository.findFirstByUserID(userId)).thenReturn(null);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> cartService.updateCart(userId, cartDetailsId, newQuantity));
        assertEquals("Cart not found for user with ID: " + userId, exception.getMessage());
    }

    @Test
    void testUpdateCart_CartDetailsNotFound() {
        Long userId = 12L;
        Long cartDetailsId = 7L;
        int newQuantity = 5;
        Cart existingCart = Cart.builder().id(6L).userID(userId).totalCost(120).build();
        existingCart.setCartDetails(new ArrayList<>());
        when(cartRepository.findFirstByUserID(userId)).thenReturn(existingCart);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> cartService.updateCart(userId, cartDetailsId, newQuantity));
        assertEquals("Cart details not found with ID: " + cartDetailsId, exception.getMessage());
    }

    @Test
    void testDeleteFromCart_Success() {
        Long userId = 20L;
        Long cartDetailsId = 15L;
        CartDetails detailsToDelete = CartDetails.builder()
                .id(cartDetailsId).cartID(10L).storeID(22L).itemID(112L)
                .name("Delete Item").quantity(3).cost(40).discount(0).build();

        Cart existingCart = Cart.builder().id(10L).userID(userId).totalCost(120)
                .cartDetails(new ArrayList<>(List.of(detailsToDelete)))
                .build();

        when(cartRepository.findFirstByUserID(userId)).thenReturn(existingCart);
        cartService.deleteFromCart(userId, cartDetailsId);
        verify(cartDetailsRepository, times(1)).delete(detailsToDelete);
        verify(cartRepository, times(1)).save(existingCart);
        assertEquals(0, existingCart.getCartDetails().size());
        assertEquals(0, existingCart.getTotalCost());
    }

    @Test
    void testDeleteFromCart_CartNotFound() {
        Long userId = 21L;
        Long cartDetailsId = 16L;
        when(cartRepository.findFirstByUserID(userId)).thenReturn(null);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> cartService.deleteFromCart(userId, cartDetailsId));
        assertEquals("Cart not found for user with ID: " + userId, exception.getMessage());
    }

    @Test
    void testDeleteFromCart_CartDetailsNotFound() {
        Long userId = 22L;
        Long cartDetailsId = 17L;

        Cart existingCart = Cart.builder().id(11L).userID(userId).totalCost(120).build();
        existingCart.setCartDetails(new ArrayList<>());
        when(cartRepository.findFirstByUserID(userId)).thenReturn(existingCart);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> cartService.deleteFromCart(userId, cartDetailsId));
        assertEquals("Cart details not found with ID: " + cartDetailsId, exception.getMessage());
    }


}
