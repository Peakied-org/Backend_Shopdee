package com.peak.main.model;

import com.peak.Util.Status;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "order_Details")
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
