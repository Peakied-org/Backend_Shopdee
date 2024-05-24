package com.peak.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long storeID;
    private Integer discount;
    private Integer cost;
    private String category;
    private String detail;
    private Integer stock;
    private Integer sold;

    @OneToMany(mappedBy = "itemID", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Type> types;

    @OneToMany(mappedBy = "itemID", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Image> images;

    @JsonProperty("types")
    public List<String> getType() {
        return types.stream().map(Type::getType).toList();
    }

    @JsonProperty("images")
    public List<String> getImage() {
        return images.stream().map(Image::getLink).toList();
    }
}
