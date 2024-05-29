package com.peak.Service;

import com.peak.main.model.Item;
import com.peak.main.repository.ImageRepository;
import com.peak.main.repository.ItemRepository;
import com.peak.main.repository.TypeRepository;
import com.peak.main.request.RequestItem;
import com.peak.main.service.ItemService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ItemService itemService;

    private final ArrayList<Item> items = new ArrayList<>(List.of(
            new Item(21L, "name1", 1L, 20, 21, "category1", "detail1", 1, 1, new ArrayList<>(), new ArrayList<>()),
            new Item(22L, "name2", 1L, 20, 22, "category2", "detail2", 1, 1, new ArrayList<>(), new ArrayList<>()),
            new Item(23L, "name3", 1L, 20, 23, "category3", "detail3", 1, 1, new ArrayList<>(), new ArrayList<>())
    ));

    @Test
    void TestGetAllItem() {

        when(itemRepository.findAll()).thenReturn(items);

        assertEquals(items, itemService.findAll());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void TestSaveItem() {

        Item item = new Item(null, "name4", 1L, 20, 23, "category4", "detail4", 1, 1, new ArrayList<>(), new ArrayList<>());

        RequestItem requestItem = new RequestItem("name4", 1L, 20, 23, "category4", "detail4", 1, 1, new ArrayList<>(), new ArrayList<>());

        assertEquals(item, itemService.save(requestItem));

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void TestUpdateItem() {
        RequestItem requestItem = new RequestItem("name4", 1L, 20, 21, "category1", "detail1", 1, 1, new ArrayList<>(), new ArrayList<>());

        Item item = items.get(0);
        item.setName("name4");

        when(itemService.findById(any(long.class))).then(invocationOnMock -> items.stream().filter(item1 -> item1.getId() == invocationOnMock.getArgument(0)).findFirst());

        assertEquals(item, itemService.updateItem(requestItem, 21L));

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void TestFindById() {

        when(itemRepository.findById(any(long.class))).then(invocationOnMock -> items.stream().filter(item -> item.getId() == invocationOnMock.getArgument(0)).findFirst());

        assertEquals(Optional.of(items.get(0)), itemService.findById(21L));

        verify(itemRepository, times(1)).findById(any(long.class));
    }

    @Test
    void TestDeleteById() {

        doNothing().when(itemRepository).deleteById(any(long.class));

        itemService.deleteById(21L);

        verify(itemRepository, times(1)).deleteById(any(long.class));
    }
}
