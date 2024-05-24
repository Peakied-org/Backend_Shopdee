package com.peak.main.model;

import com.peak.util.Status;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sold_item")
public class SoldItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Status status;
}
