package com.demo.aws.cloudwatch;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class CloudwatchApplicationTests {

	static SimpleMeterRegistry simpleMeterRegistry;

	@BeforeAll
	public static void setUp(){
		simpleMeterRegistry = new SimpleMeterRegistry();
	}
	@Test
	public void givenGlobalRegistry_whenIncrementAnywhere_thenCounted() {
		class CountedObject {
			private CountedObject() {
				Metrics.counter("objects.instance").increment(1.0);
			}
		}
		Metrics.addRegistry(new SimpleMeterRegistry());

		Metrics.counter("objects.instance").increment();
		new CountedObject();

		Optional<Counter> counterOptional = Optional.ofNullable(Metrics.globalRegistry
				.find("objects.instance").counter());
		assertTrue(counterOptional.isPresent());
		assertTrue(counterOptional.get().count() == 2.0);
	}

	@Test
	public void givenCounter_whenIncrement_thenIncrementCounter(){
		Counter counter = Counter
				.builder("instance")
				.description("indicates instance count of the object")
				.tags("dev", "performance")
				.register(simpleMeterRegistry);

		counter.increment(2.0);
        assertEquals(2, counter.count());
		counter.increment(1);
        assertEquals(3, counter.count());

	}

	@Test
	public void givenTimer_whenRecordTime_thenSetTimer(){
		Timer timer = simpleMeterRegistry.timer("app.time");
		timer.record(() -> {
			try {
				TimeUnit.MILLISECONDS.sleep(15);
			} catch (InterruptedException e){

			}
		});

		timer.record(30, TimeUnit.MILLISECONDS);
		assertEquals(2, timer.count());
		assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isBetween(40.0, 55.0);

	}

	@Test
	public void givenTimer_whenLongTaskRecordTime_thenSetLongTaskTimer(){
		LongTaskTimer longTaskTimer = LongTaskTimer
				.builder("3rdPartyService")
				.register(simpleMeterRegistry);

		LongTaskTimer.Sample currentTaskId = longTaskTimer.start();
		try {
			TimeUnit.MILLISECONDS.sleep(2);
		} catch (InterruptedException ignored) { }
		long timeElapsed = currentTaskId.stop();

		assertEquals(2L, timeElapsed/((int) 1e6),1L);

	}

	@Test
	public void givenGauge_whenIncrementValue_thenSetGauge(){
		List<String> list = new ArrayList<>(4);

		Gauge gauge = Gauge
				.builder("cache.size", list, List::size)
				.register(simpleMeterRegistry);

        assertEquals(0.0, gauge.value());
		list.add("FirstElement");
		list.add("SecondElement");
        assertEquals(2.0, gauge.value());
	}

	@Test
	public void givenDistributionSummary_whenAddRecord_thenIncrementTotal(){
		DistributionSummary distributionSummary = DistributionSummary
				.builder("request.size")
				.baseUnit("bytes")
				.register(simpleMeterRegistry);

		distributionSummary.record(3);
		distributionSummary.record(4);
		distributionSummary.record(5);

        assertEquals(3, distributionSummary.count());
        assertEquals(12, distributionSummary.totalAmount());
	}

	@Test
	public void givenTimer_whenPublishPercentiles_thenPercentilesAdded(){
		Timer timer = Timer
			.builder("test.timer")
			.publishPercentiles(0.3, 0.5, 0.95)
			.publishPercentileHistogram()
			.register(simpleMeterRegistry);

		addPercentiles(timer);

		Map<Double, Double> actualMicrometer = new TreeMap<>();
		ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();
		for (ValueAtPercentile percentile : percentiles) {
			actualMicrometer.put(percentile.percentile(), percentile.value(TimeUnit.MILLISECONDS));
		}

		Map<Double, Double> expectedMicrometer = new TreeMap<>();
		expectedMicrometer.put(0.3, 1946.157056);
		expectedMicrometer.put(0.5, 3019.89888);
		expectedMicrometer.put(0.95, 13354.663936);

		assertEquals(expectedMicrometer, actualMicrometer);
	}

	private void addPercentiles(Timer timer){
		timer.record(2, TimeUnit.SECONDS);
		timer.record(2, TimeUnit.SECONDS);
		timer.record(3, TimeUnit.SECONDS);
		timer.record(4, TimeUnit.SECONDS);
		timer.record(8, TimeUnit.SECONDS);
		timer.record(13, TimeUnit.SECONDS);

	}
}
