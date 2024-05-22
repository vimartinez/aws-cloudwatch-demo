package com.demo.aws.cloudwatch.aspect;

import com.demo.aws.cloudwatch.config.CloudWatchRegistry;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class SMAspect {

    @Value("${localstack.cloudwatch.endpoint}")
    private String cloudWatchEndpoint;

    //  private final Timer timer = getTimer();

    private Instant start;
    private long startTime;
/*
    @Before("execution(* org.jpos.jcard.sm.client.SMClient.*(..))")
    public synchronized void beforeSMClient(JoinPoint joinPoint) {

        String methodName = joinPoint.getSignature().getName();

        Timer timer = CloudWatchRegistry.getTimer();
        startTime = System.currentTimeMillis();
        timer.record(startTime, TimeUnit.MILLISECONDS);

        start= Instant.now();
        System.out.println("Method: " + methodName + " startTime: " + startTime  );


    }


    @After("execution(* org.jpos.jcard.sm.client.SMClient.*(..))")
    public synchronized void afterSMClient(JoinPoint joinPoint) throws InterruptedException {

        String methodName = joinPoint.getSignature().getName();

        Random random = new Random();
        int milliseconds = random.nextInt(1000);
        Thread.sleep(milliseconds);

        Timer timer = CloudWatchRegistry.getTimer();
        long endTime = System.currentTimeMillis();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);;
     //   timer.record(duration);

        timer.record(endTime, TimeUnit.MILLISECONDS);
        System.out.println("Method: " + methodName + " endTime: " + endTime + " duration: " + duration);

    }
*/

    @Around("execution(* com.demo.aws.cloudwatch.controller.ProductController.*(..))")
    public Object logRequestTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        Timer.Sample sample = Timer.start(CloudWatchRegistry.getMeterRegistry()); // init timer sample (other timer)

        Random random = new Random();
        int milliseconds = random.nextInt(1000);
        Thread.sleep(milliseconds);

        Object result = joinPoint.proceed();
        sample.stop(CloudWatchRegistry.getMeterRegistry().timer("other.timer", "method",methodName)); // stop sample
        long executionTime = System.currentTimeMillis() - startTime;
        CloudWatchRegistry.getTimer().record(executionTime, TimeUnit.MILLISECONDS); // timer one
        CloudWatchRegistry.getCounter().increment();
        System.out.println("Method: " + methodName + " time: " + executionTime );

        return result;
    }

}
