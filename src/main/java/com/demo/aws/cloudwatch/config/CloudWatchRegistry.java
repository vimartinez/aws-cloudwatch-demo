package com.demo.aws.cloudwatch.config;


import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

public class CloudWatchRegistry {

    @Getter
    private static final MeterRegistry meterRegistry = initMeterRegistry();
    @Getter
    private static final Timer timer = initTimer(CloudWatchRegistry.getMeterRegistry());
    @Getter
    private static final Counter counter = setCounter(CloudWatchRegistry.getMeterRegistry());


    private static MeterRegistry initMeterRegistry() {
        CloudWatchConfig cloudWatchConfig = setupCloudWatchConfig();

        CloudWatchMeterRegistry cloudWatchMeterRegistry =
                new CloudWatchMeterRegistry(
                        cloudWatchConfig,
                        Clock.SYSTEM,
                        cloudWatchAsyncClient());

      // cloudWatchMeterRegistry.config().commonTags("issuer", issuer, "service", service);
      // Metrics.addRegistry(cloudWatchMeterRegistry);
        return cloudWatchMeterRegistry;
    }

    private static CloudWatchConfig setupCloudWatchConfig() {
        CloudWatchConfig cloudWatchConfig = new CloudWatchConfig() {

            private Map<String, String> configuration = Map.of(
                    "cloudwatch.namespace", "digital-space",
                    "cloudwatch.step", Duration.ofMinutes(1).toString());

            @Override
            public String get(String key) {
                return configuration.get(key);
            }
        };
        return cloudWatchConfig;
    }

    private static CloudWatchAsyncClient cloudWatchAsyncClient() {
        return CloudWatchAsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .endpointOverride(URI.create("https://localhost.localstack.cloud:4566/"))
                .build();
    }


    private static Timer initTimer(MeterRegistry registry){
        Timer timer = Timer.builder("one.timer")
                .description("API Method Timer")
                .percentilePrecision(3)
                .publishPercentiles(0.8,0.9,0.95,0.99)
//                .publishPercentileHistogram()
//                .tags("region", "test")
                .register(registry);
        return timer;
    }

    private static Counter setCounter(MeterRegistry registry){
        Counter counter = Counter
                .builder("calls")
                .description("count total calls")
                .tags("counter", "performance")
                .register(registry);
        return counter;
    }
}
