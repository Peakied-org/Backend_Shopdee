package com.peak.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long userID;
    private String detail;
    private String image;
    private String banner;

    @OneToMany(mappedBy = "storeID", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Item> items;
}
