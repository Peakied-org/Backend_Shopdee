package com.peak.main.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String image;
}
