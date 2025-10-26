package com.spring.jpa.stream.controller;


import com.spring.jpa.stream.entity.Stock;
import com.spring.jpa.stream.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@RestController
@RequestMapping("/reactive/stocks")
public class ReactiveStockController {

    @Autowired
    private StockService stockService;


    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Stock> streamStocks(){
        return stockService.streamStocks();
    }

    @GetMapping(value = "/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamStockPrice(){
        return Flux.interval(Duration.ofSeconds(1))
                .take(20)
                .map(i->{
                    double price = 100 + new Random().nextDouble() * 10;
                    return "Stock Price: " + price;
                });
    }
}