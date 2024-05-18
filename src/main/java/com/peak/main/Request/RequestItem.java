package com.peak.main.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestItem {
    private String name;
    private Long storeID;
    private Integer discount;
    private Integer cost;
    private String category;
    private String detail;
    private Integer stock;
    private Integer sold;
    private List<String> types;
    private List<String> images;
}