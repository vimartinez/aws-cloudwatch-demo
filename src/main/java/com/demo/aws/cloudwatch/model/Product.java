package com.demo.aws.cloudwatch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String name;
    private String brand;
    private String type;
    private Double price;

}