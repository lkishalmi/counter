package com.lkishalmi.counter;

import com.lkishalmi.counter.CounterService.Counter;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;

/**
 *
 * @author lkishalmi
 */
public interface CounterRepository {
    Optional<Counter> load(@NonNull String name);
    void save(Counter c);
}
