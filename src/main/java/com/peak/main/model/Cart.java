package com.peak.main.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userID;
    private Integer totalCost;

    @OneToMany(mappedBy = "cartID", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CartDetails> cartDetails;

}
