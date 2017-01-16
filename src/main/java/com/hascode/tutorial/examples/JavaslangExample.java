package com.hascode.tutorial.examples;

import java.time.Duration;
import java.time.ZonedDateTime;

import com.hascode.tutorial.UnstableApplication;

import io.github.robwin.circuitbreaker.CircuitBreaker;
import io.github.robwin.circuitbreaker.CircuitBreakerConfig;
import io.github.robwin.decorators.Decorators;
import javaslang.control.Try;

/**
 * Sample code for Javaslang: https://github.com/RobWin/javaslang-circuitbreaker
 */
public class JavaslangExample {

    public static void main(String[] args) throws Exception {
        UnstableApplication app = new UnstableApplication();

        CircuitBreakerConfig breakerConfig = CircuitBreakerConfig.custom().ringBufferSizeInClosedState(2)
                .ringBufferSizeInHalfOpenState(2).failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000)).build();
        CircuitBreaker breaker = CircuitBreaker.of("unstableAppBreaker", breakerConfig);

        Try.CheckedSupplier<String> decoratedSupplier = Decorators.ofCheckedSupplier(app::generateId)
                .withCircuitBreaker(breaker).decorate();

        System.out.printf("circuit-breaker state is: %s\n", breaker.getState());
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            Try<String> result = Try.of(decoratedSupplier)
                    .onSuccess((a) -> System.out.printf("call succeeded, circuit-breaker state is: '%s'\n",
                            breaker.getState()))
                    .onFailure(e -> System.err.printf(
                            "failed with exception: '%s' at '%s', circuit-breaker state is: '%s'\n", e,
                            ZonedDateTime.now(), breaker.getState()));
            if (!result.isEmpty()) {
                System.out.printf("JavaslangExample: id '%s' received at '%s'\n", result.get(), ZonedDateTime.now());
            }
        }
    }

}
