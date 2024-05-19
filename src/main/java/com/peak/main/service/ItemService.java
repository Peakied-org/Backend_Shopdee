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
import java.util.Optional;

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
    
    public Item updateItem(RequestItem updateItem, long id) {
        Optional<Item> itemFound = findById(id);
        if (itemFound.isEmpty()) return null;
        Item item = getItem(updateItem, itemFound);
        if (item == null) return null;

        itemRepository.save(item);

        //////////////////////////  query type  //////////////////////////
        List<String> newTypeNames = updateItem.getTypes();
        List<Type> existingTypes = item.getTypes();

        List<Type> typesToAdd = newTypeNames.stream()
                .filter(typeName -> existingTypes.stream().noneMatch(type -> type.getType().equals(typeName)))
                .map(typeName -> typeRepository.save(Type.builder().itemID(item.getId()).type(typeName).build()))
                .toList();

        List<Type> typesToRemove = existingTypes.stream()
                .filter(type -> !newTypeNames.contains(type.getType()))
                .toList();

        typeRepository.deleteAll(typesToRemove);

        existingTypes.removeAll(typesToRemove);
        existingTypes.addAll(typesToAdd);

        //////////////////////////  query image  //////////////////////////
        List<String> newImageUrls = updateItem.getImages();
        List<Image> existingImages = item.getImages();

        List<Image> imagesToAdd = newImageUrls.stream()
                .filter(imageUrl -> existingImages.stream().noneMatch(image -> image.getImage().equals(imageUrl)))
                .map(imageUrl -> imageRepository.save(Image.builder().itemID(item.getId()).image(imageUrl).build()))
                .toList();

        List<Image> imagesToRemove = existingImages.stream()
                .filter(image -> !newImageUrls.contains(image.getImage()))
                .toList();

        imageRepository.deleteAll(imagesToRemove);

        existingImages.removeAll(imagesToRemove);
        existingImages.addAll(imagesToAdd);

        item.setImages(existingImages);
        return item;
    }

    private static Item getItem(RequestItem updateItem, Optional<Item> itemFound) {
        if (itemFound.isEmpty()) return null;
        Item item = itemFound.get();

        if (updateItem.getName() != null) item.setName(updateItem.getName());
        if (updateItem.getDiscount() != null) item.setDiscount(updateItem.getDiscount());
        if (updateItem.getCost() != null) item.setCost(updateItem.getCost());
        if (updateItem.getCategory() != null) item.setCategory(updateItem.getCategory());
        if (updateItem.getDetail() != null) item.setDetail(updateItem.getDetail());
        if (updateItem.getStock() != null) item.setStock(updateItem.getStock());
        if (updateItem.getSold() != null) item.setSold(updateItem.getSold());
        return item;
    }

    public Optional<Item> findById(long id) {
        return itemRepository.findById(id);
    }

    public void deleteById(long id) {
        itemRepository.deleteById(id);
    }
    
    
}
