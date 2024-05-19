package com.peak.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    @OneToMany(mappedBy = "itemID", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Type> types;

    @OneToMany(mappedBy = "itemID", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Image> images;

//  to output ez use
    @JsonProperty("types")
    public List<String> getType() {
        return types.stream().map(Type::getType).collect(Collectors.toList());
    }

    @JsonProperty("images")
    public List<String> getImage() {
        return images.stream().map(Image::getImage).collect(Collectors.toList());
    }
}
