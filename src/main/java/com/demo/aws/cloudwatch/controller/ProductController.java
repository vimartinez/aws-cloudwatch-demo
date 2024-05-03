package com.demo.aws.cloudwatch.controller;


import com.demo.aws.cloudwatch.metric.MetricPublisher;
import com.demo.aws.cloudwatch.metric.MetricTag;
import com.demo.aws.cloudwatch.model.Product;
import com.demo.aws.cloudwatch.service.ProductService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@Slf4j
public class ProductController {

    private Integer numberOfCalls = 0;
    private Counter pageViewsCounter;
    private MeterRegistry meterRegistry;

    private Gauge priceGauge;

    @Autowired
    ProductController(MeterRegistry meterRegistry){

        this.meterRegistry = meterRegistry;
        pageViewsCounter = meterRegistry
                .counter("PAGE_VIEWS.ProductList");


        double price  = new Random().nextDouble() * 100;
        priceGauge = Gauge
                .builder("product.price", price ,
                        (pe)->{
                            return pe != null?
                                    price : null;}
                )
                .description("Product price")
                .baseUnit("ms")
                .register(meterRegistry);
    }


    @Autowired
    ProductService productService;
    @Autowired
    private MetricPublisher metricPublisher; //send metrics with AWS SDK

    @GetMapping("/products")
    @ResponseBody
    public List<Product> fetchProducts() throws InterruptedException {

        numberOfCalls++;
        List<Product> products = productService.getAllProducts();

        sendMetricToAWSWithSDK(numberOfCalls);
        sendMetricsToAWSWithMicometer();
        return products;

    }


    private void sendMetricToAWSWithSDK(Integer numberOfCalls){

        Double metricValue = numberOfCalls.doubleValue();
        List<MetricTag> metricTags = Arrays.asList(
                new MetricTag("Product", "1"),
                new MetricTag("Calls", metricValue.toString())
        );

        log.info("Sending metric to aws with sdk");
        metricPublisher.putMetricData("Product", "Products", metricValue, metricTags);

    }

    private void sendMetricsToAWSWithMicometer(){
        log.info("Sending metric to aws with Micrometer");
        pageViewsCounter.increment();
    }

}

