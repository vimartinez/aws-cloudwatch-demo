package com.demo.aws.cloudwatch.repository;

import com.demo.aws.cloudwatch.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {

    /* mock getProducts from datasource */
    public List<Product> getAllProducts(){
        List<Product> products = new ArrayList<Product>();
        products.add(Product.builder().name("Bravia KDL65").brand("Sony").type("TV").price(1250.5).build());
        products.add(Product.builder().name("Edge HDR 55").brand("Samsung").type("TV").price(1100.0).build());
        products.add(Product.builder().name("SoundBar7.1").brand("Samsung").type("Audio").price(900.0).build());
        return products;
    }

}
