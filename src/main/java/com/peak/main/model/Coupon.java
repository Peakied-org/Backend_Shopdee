package com.peak.main.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer discount;
    private String image;

}
