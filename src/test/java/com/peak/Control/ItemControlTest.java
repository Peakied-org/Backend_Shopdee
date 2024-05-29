package com.peak.Control;

import com.peak.main.model.Item;
import com.peak.main.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final ArrayList<Item> items = new ArrayList<>(List.of(
            new Item(21L, "name1", 1L, 20, 21, "category1", "detail1", 1, 1, new ArrayList<>(), new ArrayList<>()),
            new Item(22L, "name2", 1L, 20, 22, "category2", "detail2", 1, 1, new ArrayList<>(), new ArrayList<>()),
            new Item(23L, "name3", 1L, 20, 23, "category3", "detail3", 1, 1, new ArrayList<>(), new ArrayList<>())
    ));

    @Test
    void TestGetAllItems() throws Exception {

        when(itemService.findAll()).thenReturn(items);

        mockMvc.perform(get("/item"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body[0].name").value("name1"),
                        jsonPath("$.body[0].category").value("category1"),
                        jsonPath("$.body[0].detail").value("detail1"),
                        jsonPath("$.body[1].name").value("name2"),
                        jsonPath("$.body[1].category").value("category2"),
                        jsonPath("$.body[1].detail").value("detail2"),
                        jsonPath("$.body[2].name").value("name3"),
                        jsonPath("$.body[2].category").value("category3"),
                        jsonPath("$.body[2].detail").value("detail3")
                );
        verify(itemService, times(1)).findAll();
    }

    @Test
    void TestGetItemById() throws Exception {

        when(itemService.findById(any(long.class))).then(invocationOnMock -> items.stream().filter(itemfilter -> itemfilter.getId() == invocationOnMock.getArgument(0)).findFirst());

        mockMvc.perform(get("/item/22"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.body.name").value("name2"),
                        jsonPath("$.body.category").value("category2"),
                        jsonPath("$.body.detail").value("detail2")
                );
        verify(itemService, times(1)).findById(any(long.class));
    }
}
