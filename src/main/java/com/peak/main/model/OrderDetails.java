package com.peak.main.model;

import com.peak.util.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
