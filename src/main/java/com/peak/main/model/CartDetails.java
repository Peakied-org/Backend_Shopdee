package com.peak.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart_details")
public class CartDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cartID;
    private Long storeID;
    private Long itemID;
    private String name;
    private Integer quantity;
    private Integer cost;
    private Integer discount;
    private String image;
    private String type;
}

