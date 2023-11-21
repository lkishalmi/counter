package com.lkishalmi.counter;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.validation.Validated;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

/**
 *
 * @author lkishalmi
 */
@Controller("/counter")
@Validated
public class CounterController {
    
    final CounterService counter;
    
    public CounterController(CounterService counter) {
        this.counter = counter;
    }
    
    @Produces(MediaType.TEXT_PLAIN)
    @Get("/get/{name}{?prefix,start}")
    public String increment(@NotBlank String name, Optional<String> prefix, Optional<Integer> start) {
        int startValue = start.orElse(0);
        int value = counter.increment(name, startValue);
        return prefix.isPresent() ? prefix.get() + value : String.valueOf(value);
    }
}
