package com.demo.aws.cloudwatch.controller;


import com.demo.aws.cloudwatch.metric.MetricPublisher;
import com.demo.aws.cloudwatch.metric.MetricTag;
import com.demo.aws.cloudwatch.model.Product;
import com.demo.aws.cloudwatch.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class ProductController {

    private Integer numberOfCalls = 0;

    @Autowired
    ProductService productService;
    @Autowired
    private MetricPublisher metricPublisher;

    @GetMapping("/products")
    @ResponseBody
    public List<Product> fetchProducts() throws InterruptedException {

        List<Product> products = productService.getAllProducts();

        numberOfCalls++;
        sendMetricToAWS(numberOfCalls);
        return products;

    }


    private void sendMetricToAWS(Integer numberOfCalls){

        Double metricValue = numberOfCalls.doubleValue();
        List<MetricTag> metricTags = Arrays.asList(
                new MetricTag("Product", "1"),
                new MetricTag("Calls", metricValue.toString())
        );

        log.info("Sending metric to aws");
        metricPublisher.putMetricData("Product", "Products", metricValue, metricTags);

    }

}

