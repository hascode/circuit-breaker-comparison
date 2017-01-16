package com.hascode.tutorial.examples;

import java.time.ZonedDateTime;

import com.hascode.tutorial.SampleException;
import com.hascode.tutorial.UnstableApplication;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;

/**
 * Sample Code for Vert.x: http://vertx.io/docs/vertx-circuit-breaker/java/
 */
public class VertxExample {

    public static void main(String[] args) throws Exception {
        UnstableApplication app = new UnstableApplication();

        Vertx vertx = Vertx.vertx();
        CircuitBreaker breaker = CircuitBreaker
                .create("unstableAppBreaker", vertx,
                        new CircuitBreakerOptions().setMaxFailures(2).setTimeout(2000).setFallbackOnFailure(true)
                                .setResetTimeout(2000))
                .openHandler(h -> System.err.println("circuit-breaker opened"))
                .closeHandler(h -> System.out.println("circuit-breaker closed"))
                .halfOpenHandler(h -> System.err.println("circuit-breaker half-opened"));

        System.out.printf("circuit-breaker state is: %s\n", breaker.state());
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            breaker.<String> execute(future -> {
                try {
                    final String id = app.generateId();
                    future.complete(id);
                } catch (SampleException e) {
                    future.fail(e);
                }
                if (future.failed()) {
                    System.err.printf("failed with exception: '%s' at '%s', circuit-breaker state is: '%s'\n",
                            future.cause(), ZonedDateTime.now(), breaker.state());
                }
            }).setHandler(id -> {
                if (id.succeeded())
                    System.out.printf("FailsafeExample: id '%s' received at '%s'\n", id, ZonedDateTime.now());
            });
        }

        vertx.close();
    }

}
