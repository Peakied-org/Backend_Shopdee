package com.peak.Control;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.model.Cart;
import com.peak.main.model.CartDetails;
import com.peak.main.request.Response;
import com.peak.main.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;
    private final Cart cart = new Cart(1L, 1L, 100, new ArrayList<>());
    private final CartDetails cartDetail = CartDetails.builder()
            .storeID(1L)
            .itemID(1L)
            .name("name")
            .quantity(1)
            .cost(100)
            .discount(10)
            .image("image.png")
            .type("type")
            .build();

    @Test
    @WithUserDetails
    @WithMockUser(authorities = { "USER" })
    void testGetCart() throws Exception {
        when(cartService.getCartsByUserId(anyLong())).thenReturn(cart);

        String expectedJson = objectMapper.writeValueAsString(new Response(cart));

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson));
    }

    @Test
    @WithUserDetails
    @WithMockUser(authorities = { "USER" })
    void testAddCartItem() throws Exception {
        Cart updatedCart = cart;
        updatedCart.setTotalCost(200);
        CartDetails updatedCartDetail = cartDetail;
        updatedCartDetail.setQuantity(2);
        updatedCartDetail.setCartID(updatedCart.getId());
        updatedCart.getCartDetails().add(cartDetail);
        when(cartService.addToCart(anyLong(), anyLong(), anyInt(), anyString())).thenReturn(cart);

        mockMvc.perform(post("/cart/{itemId}", 1L)
                        .param("quantity", "2")
                        .param("type", "type"))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.body.id").value(1),
                        jsonPath("$.body.userID").value(1),
                        jsonPath("$.body.totalCost").value(200),
                        jsonPath("$.body.cartDetails[0].itemID").value(1),
                        jsonPath("$.body.cartDetails[0].storeID").value(1),
                        jsonPath("$.body.cartDetails[0].cartID").value(1L),
                        jsonPath("$.body.cartDetails[0].name").value("name"),
                        jsonPath("$.body.cartDetails[0].quantity").value(2),
                        jsonPath("$.body.cartDetails[0].cost").value(100),
                        jsonPath("$.body.cartDetails[0].discount").value(10),
                        jsonPath("$.body.cartDetails[0].image").value("image.png"),
                        jsonPath("$.body.cartDetails[0].type").value("type")
                );
        verify(cartService, times(1)).addToCart(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    @WithUserDetails
    @WithMockUser
    void testAddCartItem_InvalidQuantity() throws Exception {
        mockMvc.perform(post("/cart/{itemId}", 1L)
                        .param("quantity", "-1")
                        .param("type", "type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.body").value("Invalid quantity. Quantity must be greater than 0."));
    }

    @Test
    @WithUserDetails
    @WithMockUser(authorities = { "USER" })
    void testAddCartItem_ItemNotFound() throws Exception {
        long itemId = 999L;
        when(cartService.addToCart(anyLong(), anyLong(), anyInt(), anyString()))
                .thenThrow(new EntityNotFoundException("Item not found with ID: " + itemId));
        mockMvc.perform(post("/cart/{itemId}", itemId)
                        .param("quantity", "2")
                        .param("type", "type"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.body").value("Item not found with ID: " + itemId));
    }


    @Test
    @WithUserDetails
    @WithMockUser(authorities = { "USER" })
    void testUpdateCartItem_Success() throws Exception {
        int newQuantity = 5;
        CartDetails updatedCartDetail = CartDetails.builder().id(1L).storeID(1L).itemID(1L).name("name").quantity(newQuantity).cost(100).discount(10).image("image.png").type("type").build();
        Cart updatedCart = cart;
        updatedCart.setTotalCost(500);
        updatedCart.getCartDetails().add(updatedCartDetail);

        when(cartService.updateCart(anyLong(), eq(updatedCartDetail.getId()), eq(newQuantity))).thenReturn(updatedCart);

        String expectedJson = objectMapper.writeValueAsString(new Response(updatedCart));

        mockMvc.perform(put("/cart/{cartDetailsId}", updatedCartDetail.getId())
                        .param("quantity", String.valueOf(newQuantity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(expectedJson));
        verify(cartService, times(1)).updateCart(anyLong(), eq(updatedCartDetail.getId()), eq(newQuantity));
    }


    @Test
    @WithUserDetails
    @WithMockUser
    void testDeleteCartItem_Success() throws Exception {
        long cartDetailsId = 1L;
        mockMvc.perform(delete("/cart/{cartDetailsId}", cartDetailsId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.body").value("Item removed from cart"));
        verify(cartService).deleteFromCart(anyLong(), eq(cartDetailsId));
    }

    @Test
    @WithUserDetails
    @WithMockUser
    void testDeleteCartItem_ItemNotFound() throws Exception {
        long cartDetailsId = 999L;
        String errorMessage = "Cart details not found with ID: " + cartDetailsId;

        doThrow(new EntityNotFoundException(errorMessage))
                .when(cartService).deleteFromCart(anyLong(), eq(cartDetailsId));

        mockMvc.perform(delete("/cart/{cartDetailsId}", cartDetailsId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.body").value(errorMessage));
    }

}
