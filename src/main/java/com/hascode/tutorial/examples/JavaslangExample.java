package com.hascode.tutorial.examples;

import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

import java.time.Duration;
import java.time.ZonedDateTime;

import com.hascode.tutorial.SampleException;
import com.hascode.tutorial.UnstableApplication;

import io.github.robwin.circuitbreaker.CircuitBreaker;
import io.github.robwin.circuitbreaker.CircuitBreakerConfig;
import io.github.robwin.decorators.Decorators;
import io.github.robwin.retry.Retry;
import io.github.robwin.retry.RetryConfig;
import javaslang.control.Try;

public class JavaslangExample {

    public static void main(String[] args) throws Exception {
        UnstableApplication app = new UnstableApplication(11);

        CircuitBreakerConfig breakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(1).recordFailure(throwable -> Match(throwable)
                        .of(Case(instanceOf(SampleException.class), true), Case($(), false)))
                .waitDurationInOpenState(Duration.ofSeconds(1)).build();
        CircuitBreaker breaker = CircuitBreaker.of("unstableApp", breakerConfig);
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(2).waitDuration(Duration.ofSeconds(2)).build();
        Retry retryContext = Retry.of("id", retryConfig);

        Try.CheckedSupplier<String> decoratedSupplier = Decorators.ofCheckedSupplier(app::generateId)
                .withCircuitBreaker(breaker).withRetry(retryContext).decorate();

        System.out.printf("circuit-breaker state is: %s\n", breaker.getState());
        for (int i = 0; i < 20; i++) {
            Thread.sleep(1000);
            Try<String> result = Try.of(decoratedSupplier)
                    .onSuccess((a) -> System.out.printf("call succeeded, circuit-breaker state is: '%s'\n",
                            breaker.getState()))
                    .onFailure(e -> System.err.printf(
                            "failed with exception: '%s' at '%s', circuit-breaker state is: '%s', fail #%s\n", e,
                            ZonedDateTime.now(), breaker.getState(), breaker.getMetrics().getNumberOfFailedCalls()));
            if (!result.isEmpty()) {
                System.out.printf("JavaslangExample: id '%s' received at '%s'\n", result.get(), ZonedDateTime.now());
            }
        }
    }

}
