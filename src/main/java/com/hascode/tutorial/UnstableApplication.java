package com.hascode.tutorial;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class UnstableApplication {
    private static final int FAILS = 4;
    private AtomicInteger failCount = new AtomicInteger(1);

    public String generateId() throws SampleException {
        if (failCount.getAndIncrement() < FAILS) {
            throw new SampleException();
        }

        final String id = UUID.randomUUID().toString();
        System.out.printf("UnstableApplication: id '%s' generated at '%s'\n", id, ZonedDateTime.now());

        return id;
    }
}
