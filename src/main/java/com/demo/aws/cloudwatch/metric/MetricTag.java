package com.demo.aws.cloudwatch.metric;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricTag {
    private String name;
    private String value;
    public MetricTag(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }
}
