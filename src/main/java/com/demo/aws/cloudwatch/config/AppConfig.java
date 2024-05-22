package com.demo.aws.cloudwatch.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Configuration
public class AppConfig {
/*
    @Value("${localstack.cloudwatch.endpoint}")
    private String cloudWatchEndpoint;


    @Bean
    public MeterRegistry getMeterRegistry() {
        return new SimpleMeterRegistry();
    }

*/
/*
    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient() {
        return CloudWatchAsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .endpointOverride(URI.create(cloudWatchEndpoint))
                .build();
    }

    @Bean
    public MeterRegistry getMeterRegistry() {
        CloudWatchConfig cloudWatchConfig = setupCloudWatchConfig();

        CloudWatchMeterRegistry cloudWatchMeterRegistry =
                new CloudWatchMeterRegistry(
                        cloudWatchConfig,
                        Clock.SYSTEM,
                        cloudWatchAsyncClient());

        return cloudWatchMeterRegistry;
    }

    private CloudWatchConfig setupCloudWatchConfig() {
        CloudWatchConfig cloudWatchConfig = new CloudWatchConfig() {

            private Map<String, String> configuration = Map.of(
                    "cloudwatch.namespace", "productsApp",
                    "cloudwatch.step", Duration.ofMinutes(1).toString());

            @Override
            public String get(String key) {
                return configuration.get(key);
            }
        };
        return cloudWatchConfig;
    }*/
}

