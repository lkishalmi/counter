package com.lkishalmi.counter;

import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author lkishalmi
 */
@Singleton
public class CounterService {
    public record Counter(String name, int value, Instant lastUsed){};
    
    final CounterRepository store;
    final Map<String, AtomicInteger> values;

    public CounterService() {
        this.values = new ConcurrentHashMap<>();
        this.store= new OnDiskCounterStore();
        
        store.loadAll().forEachRemaining((c) -> values.put(c.name, new AtomicInteger(c.value)));
    }

    public int increment(String name, int start) {
        AtomicInteger v = values.computeIfAbsent(name, k -> new AtomicInteger(start));
        int value = v.getAndIncrement();
        store.save(new Counter(name, v.get(), Instant.now()));
        return value;
    }

}
