package com.peak.Service;

import com.peak.main.model.Item;
import com.peak.main.model.Store;
import com.peak.main.repository.ItemRepository;
import com.peak.main.repository.StoreRepository;
import com.peak.main.request.RequestItem;
import com.peak.main.service.ItemService;
import com.peak.main.service.StoreService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemService itemService;

    private final ArrayList<Store> stores = new ArrayList<>(List.of(
            new Store(1L, "name", 11L, "detail1", "image1", "banner1", new ArrayList<>()),
            new Store(2L, "name", 12L, "detail2", "image2", "banner2", new ArrayList<>()),
            new Store(3L, "name", 13L, "detail3", "image3", "banner3", new ArrayList<>())
    ));

    @Test
    void TestGetAllStores() {

        when(storeRepository.findAll()).thenReturn(stores);

        assertEquals(storeService.findAll(), stores);

        verify(storeRepository, times(1)).findAll();
    }

    @Test
    void TestSaveStore() {

        when(storeRepository.save(any(Store.class))).thenReturn(stores.get(0));

        assertEquals(stores.get(0), storeService.save(stores.get(0)));

        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void TestFindStoreByUserId() {

        when(storeRepository.findByUserID(any(long.class))).then(invocationOnMock ->
                stores.stream().filter(store -> store.getUserID().equals(invocationOnMock.getArgument(0))).findFirst()
        );

        assertEquals(Optional.of(stores.get(0)), storeService.findByUserId(11L));

        verify(storeRepository, times(1)).findByUserID(any(long.class));
    }

    @Test
    void TestFindStoreByStoreId() {

        when(storeRepository.findById(any(long.class))).then(invocationOnMock ->
                stores.stream().filter(store -> store.getId().equals(invocationOnMock.getArgument(0))).findFirst()
        );

        assertEquals(Optional.of(stores.get(0)), storeService.findById(1L));

        verify(storeRepository, times(1)).findById(any(long.class));
    }

    @Test
    void TestUpdateStore() {

        Store newStore = stores.get(0);
        newStore.setName("newName");

        when(storeRepository.save(any(Store.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        assertEquals(newStore, storeService.update(stores.get(0), newStore));

        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void TestDeleteStore() {

        doNothing().when(storeRepository).deleteById(any(long.class));

        storeRepository.deleteById(1L);

        verify(storeRepository, times(1)).deleteById(any(long.class));
    }

    @Test
    void TestHasPermissionStore() {

        when(storeRepository.findByUserID(any(long.class))).then(invocationOnMock ->
                stores.stream().filter(store -> store.getUserID().equals(invocationOnMock.getArgument(0))).findFirst()
        );

        assertTrue(storeService.hasPermissionStore(11L, 1L));

        verify(storeRepository, times(1)).findByUserID(any(long.class));
    }

    @Test
    void TestHasPermissionItem() {

        Item item = new Item(21L, "name", 1L, 12, 12, "category", "detail", 1, 1, new ArrayList<>(), new ArrayList<>());

        when(itemService.findById(any(long.class))).thenReturn(Optional.of(item));

        when(storeRepository.findById(any(long.class))).then(invocationOnMock ->
                stores.stream().filter(store -> store.getId().equals(invocationOnMock.getArgument(0))).findFirst()
        );

        assertTrue(storeService.hasPermissionItem(11L, 21L));

        verify(storeRepository, times(1)).findById(any(long.class));
        verify(itemService, times(1)).findById(any(long.class));
    }

    @Test
    void TestSaveToStore() {

        when(storeRepository.save(any(Store.class))).thenReturn(stores.get(0));

        assertEquals(stores.get(0), storeService.save(stores.get(0)));

        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void TestDeleteFromStore() {

        doNothing().when(storeRepository).deleteById(any(long.class));

        storeRepository.deleteById(1L);

        verify(storeRepository, times(1)).deleteById(any(long.class));
    }

    @Test
    void TestUpdateItem() {

        Item expectedItem = Item.builder().name("newName").build();

        when(itemService.updateItem(any(RequestItem.class), anyLong())).thenReturn(expectedItem);

        // Act
        Item actualItem = storeService.updateItem(RequestItem.builder().name("newName").build(), 1L);

        // Assert
        assertEquals("newName", actualItem.getName());  // Verify the result of the updateItem method
        verify(itemService, times(1)).updateItem(any(RequestItem.class), any(long.class));
    }
}
