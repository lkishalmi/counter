package com.lkishalmi.counter;

import com.lkishalmi.counter.CounterService.Counter;
import java.util.Iterator;

/**
 *
 * @author lkishalmi
 */
public interface CounterRepository {
    Iterator<Counter> loadAll();    
    void save(Counter c);
}
