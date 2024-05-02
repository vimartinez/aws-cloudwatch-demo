package com.demo.aws.cloudwatch.service;

import com.demo.aws.cloudwatch.model.Product;
import com.demo.aws.cloudwatch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProducts(){
        return productRepository.getAllProducts();
    }

}
