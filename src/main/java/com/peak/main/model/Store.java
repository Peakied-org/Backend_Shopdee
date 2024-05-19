package com.peak.main.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long userID;
    private String detail;
    private String image;

    @OneToMany(mappedBy = "storeID", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Item> items;
}
