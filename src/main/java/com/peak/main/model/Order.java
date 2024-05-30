package com.peak.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userID;
    private LocalDateTime orderDate;
    private Integer totalCost;

    @OneToMany(mappedBy = "orderID", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderDetails> orderDetails;

}
