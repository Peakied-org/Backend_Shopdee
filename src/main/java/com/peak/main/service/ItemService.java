package com.peak.main.service;

import com.peak.main.Request.RequestItem;
import com.peak.main.model.Image;
import com.peak.main.model.Item;
import com.peak.main.model.Type;
import com.peak.main.repository.ImageRepository;
import com.peak.main.repository.ItemRepository;
import com.peak.main.repository.TypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TypeRepository typeRepository;
    private final ImageRepository imageRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item save(RequestItem item) {
        if (item.getStock() == null) item.setStock(0);
        if (item.getDiscount() == null) item.setDiscount(0);
        if (item.getSold() == null) item.setSold(0);

        Item newitem = Item.builder()
                .name(item.getName())
                .storeID(item.getStoreID())
                .discount(item.getDiscount())
                .cost(item.getCost())
                .category(item.getCategory())
                .detail(item.getDetail())
                .stock(item.getStock())
                .sold(item.getSold())
                .build();
        itemRepository.save(newitem);

        List<Type> types = new ArrayList<>();
        if (item.getTypes() != null) {
            item.getTypes().forEach(type -> {
                Type newType = Type.builder()
                        .itemID(newitem.getId())
                        .type(type)
                        .build();
                types.add(newType);
                typeRepository.save(newType);
            });
        }

        List<Image> images = new ArrayList<>();
        if (item.getImages() != null) {
            item.getImages().forEach(image -> {
                Image newImage = Image.builder()
                        .itemID(newitem.getId())
                        .image(image)
                        .build();
                images.add(newImage);
                imageRepository.save(newImage);
            });
        }
        newitem.setTypes(types);
        newitem.setImages(images);

        return newitem;
    }

    public void deleteById(long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> findByName(String name) {
        return itemRepository.findByName(name);
    }
}
