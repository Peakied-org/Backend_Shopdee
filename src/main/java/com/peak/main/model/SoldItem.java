package com.peak.main.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.peak.Util.Status;
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
