package com.lkishalmi.counter;

import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Optional;

/**
 *
 * @author lkishalmi
 */
@Singleton
public class CounterService {
    public record Counter(String name, int value, Instant lastUsed){};
    
    final CounterRepository store;

    public CounterService(CounterRepository store) {
        this.store= store;  
    }

    public int increment(String name, int start) {
        Optional<Counter> counter = store.load(name);
        int value = counter.isPresent() ? counter.get().value : start;
        store.save(new Counter(name, value + 1, Instant.now()));
        return value;
    }

}
