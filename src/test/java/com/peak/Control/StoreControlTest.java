package com.peak.Control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peak.main.model.Item;
import com.peak.main.model.Store;
import com.peak.main.request.RequestItem;
import com.peak.main.service.StoreService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<Store> stores = new ArrayList<>(List.of(
            new Store(1L, "name1", 11L, "detail1", "image1", "banner1", new ArrayList<>()),
            new Store(2L, "name2", 12L, "detail2", "image2", "banner2", new ArrayList<>()),
            new Store(3L, "name3", 13L, "detail3", "image3", "banner3", new ArrayList<>())
    )); ;

    private final Item item = Item.builder()
            .name("name")
            .cost(12)
            .category("category")
            .detail("detail")
            .types(new ArrayList<>())
            .images(new ArrayList<>())
            .build();

    @Test
    @WithMockUser
    void testGetStore() throws Exception {

        when(storeService.findAll()).thenReturn(stores);

        mockMvc.perform(get("/store"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("$.body").isArray(),
                        jsonPath("$.body[0].id").value(1),
                        jsonPath("$.body[0].name").value("name1"),
                        jsonPath("$.body[0].userID").value(11),
                        jsonPath("$.body").isArray(),
                        jsonPath("$.body[1].id").value(2),
                        jsonPath("$.body[1].name").value("name2"),
                        jsonPath("$.body[1].userID").value(12),
                        jsonPath("$.body").isArray(),
                        jsonPath("$.body[2].id").value(3),
                        jsonPath("$.body[2].name").value("name3"),
                        jsonPath("$.body[2].userID").value(13)
                );
        verify(storeService, times(1)).findAll();
    }

    @Test
    @WithMockUser
    void testGetStoreById() throws Exception {

        when(storeService.findById(any(long.class))).thenReturn(
                stores.stream().filter(store -> store.getId().equals(1L)).findFirst()
        );

        mockMvc.perform(get("/store/1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("$.body.id").value(1),
                        jsonPath("$.body.name").value("name1"),
                        jsonPath("$.body.userID").value(11)
                );
        verify(storeService, times(1)).findById(any(long.class));
    }

    @Test
    @WithUserDetails("user")
    @WithMockUser(authorities = { "USER" })
    void testCreateStoreByUser() throws Exception {

        Store create = new Store(4L, "name3", 14L, "detail4", "image4", "banner4", new ArrayList<>());

        when(storeService.save(any(Store.class))).thenReturn(create);

        String json = objectMapper.writeValueAsString(create);

        mockMvc.perform(post("/store/me")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isForbidden());
        verify(storeService, times(0)).save(any(Store.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testCreateStoreBySeller() throws Exception {

        Store create = new Store(4L, "name3", 14L, "detail4", "image4", "banner4", new ArrayList<>());

        when(storeService.save(any(Store.class))).thenReturn(create);

        String json = objectMapper.writeValueAsString(create);

        mockMvc.perform(post("/store/me")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.body.name").value("name3"),
                        jsonPath("$.body.userID").value(14),
                        jsonPath("$.body.detail").value("detail4"),
                        jsonPath("$.body.image").value("image4"),
                        jsonPath("$.body.banner").value("banner4")
                );
        verify(storeService, times(1)).save(any(Store.class));
    }

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = { "ADMIN" })
    void testCreateStoreByAdmin() throws Exception {

        Store create = new Store(4L, "name3", 14L, "detail4", "image4", "banner4", new ArrayList<>());

        when(storeService.save(any(Store.class))).thenReturn(create);

        String json = objectMapper.writeValueAsString(create);

        mockMvc.perform(post("/store/me")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.body.name").value("name3"),
                        jsonPath("$.body.userID").value(14),
                        jsonPath("$.body.detail").value("detail4"),
                        jsonPath("$.body.image").value("image4"),
                        jsonPath("$.body.banner").value("banner4")
                );
        verify(storeService, times(1)).save(any(Store.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testUpdateStoreBySeller() throws Exception {

        Store create = new Store(4L, "newName", 14L, "detail4", "image4", "banner4", new ArrayList<>());

        when(storeService.save(any(Store.class))).thenReturn(create);

        String json = objectMapper.writeValueAsString(create);

        mockMvc.perform(post("/store/me")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.body.name").value("newName"),
                        jsonPath("$.body.userID").value(14),
                        jsonPath("$.body.detail").value("detail4"),
                        jsonPath("$.body.image").value("image4"),
                        jsonPath("$.body.banner").value("banner4")
                );
        verify(storeService, times(1)).save(any(Store.class));
    }

    @Test
    @WithUserDetails("user")
    @WithMockUser(authorities = { "USER" })
    void testDeleteStoreByUser() throws Exception {

        mockMvc.perform(delete("/store/{id}", 1L))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).deleteById(any(long.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testDeleteStoreByCorrectSeller() throws Exception {

        when(storeService.hasPermissionStore(any(long.class), any(long.class))).thenReturn(true);

        mockMvc.perform(delete("/store/{id}", 1L))
                        .andExpectAll(
                                status().isOk()
                        );
        verify(storeService, times(1)).deleteById(any(long.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testDeleteStoreByIncorrectSeller() throws Exception {

        when(storeService.hasPermissionStore(any(long.class), any(long.class))).thenReturn(false);

        mockMvc.perform(delete("/store/{id}", 1L))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).deleteById(any(long.class));
    }

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = { "ADMIN" })
    void testDeleteStoreByAdmin() throws Exception {

        mockMvc.perform(delete("/store/{id}", 1L))
                .andExpectAll(
                        status().isOk()
                );
        verify(storeService, times(1)).deleteById(any(long.class));
    }

    @Test
    @WithUserDetails("user")
    @WithMockUser(authorities = { "USER" })
    void testAddItemToStoreByUser() throws Exception {

        when(storeService.hasPermissionStore(any(long.class), any(long.class))).thenReturn(false);

        String json = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/store/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).saveToStore(any(RequestItem.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testAddItemToStoreByCorrectSeller() throws Exception {

        String json = objectMapper.writeValueAsString(item);

        when(storeService.findById(any(long.class))).then(invocationOnMock -> Optional.of(
                stores.stream().filter(store -> store.getId() == invocationOnMock.getArgument(0)).findFirst())
        );

        when(storeService.hasPermissionStore(any(long.class), any(long.class))).thenReturn(true);

        when(storeService.saveToStore(any(RequestItem.class))).thenReturn(item);

        mockMvc.perform(post("/store/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.body.name").value("name"),
                        jsonPath("$.body.cost").value(12),
                        jsonPath("$.body.category").value("category"),
                        jsonPath("$.body.detail").value("detail")
                );
        verify(storeService, times(1)).saveToStore(any(RequestItem.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = { "SELLER" })
    void testAddItemToStoreByIncorrectSeller() throws Exception {

        String json = objectMapper.writeValueAsString(item);

        when(storeService.findById(any(long.class))).then(invocationOnMock -> Optional.of(
                stores.stream().filter(store -> store.getId() == invocationOnMock.getArgument(0)).findFirst())
        );

        when(storeService.hasPermissionStore(any(long.class), any(long.class))).thenReturn(false);

        mockMvc.perform(post("/store/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).saveToStore(any(RequestItem.class));
    }

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = { "ADMIN" })
    void testAddItemToStoreByAdmin() throws Exception {

        String json = objectMapper.writeValueAsString(item);

        when(storeService.findById(any(long.class))).then(invocationOnMock -> Optional.of(
                stores.stream().filter(store -> store.getId() == invocationOnMock.getArgument(0)).findFirst())
        );

        when(storeService.saveToStore(any(RequestItem.class))).thenReturn(item);

        mockMvc.perform(post("/store/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.body.name").value("name"),
                        jsonPath("$.body.cost").value(12),
                        jsonPath("$.body.category").value("category"),
                        jsonPath("$.body.detail").value("detail")
                );
        verify(storeService, times(1)).saveToStore(any(RequestItem.class));
    }

    @Test
    @WithUserDetails("user")
    @WithMockUser(authorities = "USER")
    void testUpdateItemToStoreByUser() throws Exception {

        Item oldItem = item;
        item.setCategory("newCategory");

        String json = objectMapper.writeValueAsString(item);

        when(storeService.updateItem(any(RequestItem.class),any(long.class))).then(invocationOnMock -> oldItem);

        mockMvc.perform(put("/store/item/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).updateItem(any(RequestItem.class), any(long.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = "SELLER")
    void testUpdateItemToStoreByCorrectSeller() throws Exception {

        Item oldItem = item;
        item.setCategory("newCategory");

        String json = objectMapper.writeValueAsString(item);

        when(storeService.hasPermissionItem(any(long.class), any(long.class))).thenReturn(true);

        when(storeService.updateItem(any(RequestItem.class),any(long.class))).then(invocationOnMock -> oldItem);

        mockMvc.perform(put("/store/item/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body.name").value("name"),
                        jsonPath("$.body.cost").value(12),
                        jsonPath("$.body.category").value("newCategory"),
                        jsonPath("$.body.detail").value("detail")
                );
        verify(storeService, times(1)).updateItem(any(RequestItem.class),any(long.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = "SELLER")
    void testUpdateItemToStoreByIncorrectSeller() throws Exception {

        Item oldItem = item;
        item.setCategory("newCategory");

        String json = objectMapper.writeValueAsString(item);

        when(storeService.hasPermissionItem(any(long.class), any(long.class))).thenReturn(false);

        when(storeService.updateItem(any(RequestItem.class),any(long.class))).then(invocationOnMock -> oldItem);

        mockMvc.perform(put("/store/item/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).updateItem(any(RequestItem.class), any(long.class));
    }

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = "ADMIN")
    void testUpdateItemToStoreByAdmin() throws Exception {

        Item oldItem = item;
        item.setCategory("newCategory");

        String json = objectMapper.writeValueAsString(item);

        when(storeService.updateItem(any(RequestItem.class),any(long.class))).then(invocationOnMock -> oldItem);

        mockMvc.perform(put("/store/item/1")
                        .contentType("application/json")
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body.name").value("name"),
                        jsonPath("$.body.cost").value(12),
                        jsonPath("$.body.category").value("newCategory"),
                        jsonPath("$.body.detail").value("detail")
                );
        verify(storeService, times(1)).updateItem(any(RequestItem.class),any(long.class));
    }


    @Test
    @WithUserDetails("user")
    @WithMockUser(authorities = "USER")
    void testDeleteItemByUser() throws Exception {

        doNothing().when(storeService).deleteFromStore(any(long.class));

        mockMvc.perform(delete("/store/item/1"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).deleteFromStore(any(long.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = "SELLER")
    void testDeleteItemByCorrectSeller() throws Exception {

        when(storeService.hasPermissionItem(any(long.class), any(long.class))).thenReturn(true);

        doNothing().when(storeService).deleteFromStore(any(long.class));

        mockMvc.perform(delete("/store/item/1"))
                .andExpectAll(
                        status().isOk()
                );
        verify(storeService, times(1)).deleteFromStore(any(long.class));
    }

    @Test
    @WithUserDetails("seller")
    @WithMockUser(authorities = "SELLER")
    void testDeleteItemByIncorrectSeller() throws Exception {

        when(storeService.hasPermissionItem(any(long.class), any(long.class))).thenReturn(false);

        mockMvc.perform(delete("/store/item/1"))
                .andExpectAll(
                        status().isForbidden()
                );
        verify(storeService, times(0)).deleteFromStore(any(long.class));
    }

    @Test
    @WithUserDetails("admin")
    @WithMockUser(authorities = "ADMIN")
    void testDeleteItemByAdmin() throws Exception {

        doNothing().when(storeService).deleteFromStore(any(long.class));

        mockMvc.perform(delete("/store/item/1"))
                .andExpectAll(
                        status().isOk()
                );
        verify(storeService, times(1)).deleteFromStore(any(long.class));
    }

}
