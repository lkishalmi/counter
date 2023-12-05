/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lkishalmi.counter;

import com.lkishalmi.counter.CounterService.Counter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 *
 * @author lkishalmi
 */
public class FileCounterRepository implements CounterRepository {

    @Override
    public void save(CounterService.Counter c) {
        try {
            Files.writeString(new File(c.name() + ".cnt").toPath(), String.valueOf(c.value()));
        } catch (IOException ex) {
            
        }
    }
    
    @Override
    public Optional<Counter> load(String name) {
        try {
            var p = new File(name + ".cnt").toPath();
            if (Files.exists(p) && Files.size(p) < 12) {
                int value = Integer.parseInt(Files.readString(p).trim());
                var c = new Counter(name, value, Files.getLastModifiedTime(p).toInstant()); 
                return Optional.of(c);
            }
        } catch (IOException|NumberFormatException ex) {
        }
        return Optional.empty();
    }
}
