/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lkishalmi.counter;

import com.lkishalmi.counter.CounterService.Counter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lkishalmi
 */
public class OnDiskCounterStore implements CounterRepository {

    private static final String FILE_NAME = "counters.properties";
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    
    @Override
    public Iterator<CounterService.Counter> loadAll() {
        try (var is = new FileInputStream(FILE_NAME)) {
            var props = new Properties();
            props.load(is);
            for (String  key : props.stringPropertyNames()) {
                if (key.endsWith(".value")) {
                    String name = key.substring(0, key.length() - ".value".length());
                    int value = 0;
                    Instant lastModified = Instant.now();
                    try {
                        value = Integer.parseInt(props.getProperty(key));
                        String lm = props.getProperty(name + ".lastUsed");
                        if (lm != null) {
                           lastModified = Instant.ofEpochMilli(Long.parseLong(lm));
                        }
                    } catch (NumberFormatException ex) {}
                    counters.put(name, new Counter(name, value, lastModified));
                }
            }
        } catch (IOException ex) {
            return Collections.<Counter>emptyList().iterator();
        }
        return counters.values().iterator();
    }

    @Override
    public void save(CounterService.Counter c) {
        counters.put(c.name(), c);
        saveAll();
    }
    
    private void saveAll() {
        Properties props = new Properties();
        for (Map.Entry<String, Counter> entry : counters.entrySet()) {
            Counter c = entry.getValue();
            props.put(entry.getKey() + ".value", String.valueOf(c.value()));
            props.put(entry.getKey() + ".lastUsed", String.valueOf(c.lastUsed().toEpochMilli()));
        }
        synchronized (this) {
            try (var os = new FileOutputStream(FILE_NAME)) {
                props.store(os, null);
            } catch(IOException ex) {}
        }
    }
}
