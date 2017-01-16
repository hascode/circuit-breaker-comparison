package com.hascode.tutorial.examples;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import com.hascode.tutorial.UnstableApplication;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.CircuitBreakerOpenException;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

/**
 * Sample Code for Failsafe: https://github.com/jhalterman/failsafe
 */
public class FailsafeExample {

    public static void main(String[] args) throws Exception {
        UnstableApplication app = new UnstableApplication();

        CircuitBreaker breaker = new CircuitBreaker().withFailureThreshold(2).withSuccessThreshold(5).withDelay(1,
                TimeUnit.SECONDS);
        RetryPolicy retryPolicy = new RetryPolicy().withDelay(2, TimeUnit.SECONDS).withMaxDuration(60, TimeUnit.SECONDS)
                .withBackoff(4, 40, TimeUnit.SECONDS);

        System.out.printf("circuit-breaker state is: %s\n", breaker.getState());
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            try {
                String id = Failsafe.with(breaker).with(retryPolicy)
                        .onFailedAttempt((a, b) -> System.err.printf(
                                "failed with exception: '%s' at '%s', circuit-breaker state is: '%s'\n", b,
                                ZonedDateTime.now(), breaker.getState()))
                        .onSuccess((a, b) -> System.out.printf("call succeeded, circuit-breaker state is: '%s'\n",
                                breaker.getState()))
                        .get(app::generateId);
                System.out.printf("FailsafeExample: id '%s' received at '%s'\n", id, ZonedDateTime.now());
            } catch (CircuitBreakerOpenException e) {
                System.out.printf("circuit-breaker is open (state %s), time is '%s'\n", breaker.getState(),
                        ZonedDateTime.now());
            }
        }
    }

}
