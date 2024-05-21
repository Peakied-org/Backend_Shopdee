package com.peak.main.model;

import com.peak.Util.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long orderID;
    private Long storeID;
    private Long itemID;
    private Integer quantity;
    private Integer cost;
    private String image;
    private Status status;
    private String type;
}
