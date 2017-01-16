package com.hascode.tutorial;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class UnstableApplication {
    private final int fails;
    private AtomicInteger failCount = new AtomicInteger(1);

    public UnstableApplication(int fails) {
        this.fails = fails;
    }

    public String generateId() throws SampleException {
        if (failCount.getAndIncrement() < fails) {
            throw new SampleException();
        }

        final String id = UUID.randomUUID().toString();
        System.out.printf("UnstableApplication: id '%s' generated at '%s'\n", id, ZonedDateTime.now());

        return id;
    }

}
