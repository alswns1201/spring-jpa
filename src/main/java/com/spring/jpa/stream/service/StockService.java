package com.spring.jpa.stream.service;


import com.spring.jpa.stream.entity.Stock;
import com.spring.jpa.stream.repository.StockRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<Stock> getAllStock(){
        List<Stock> stockList = stockRepository.findAll();
        log.info("stocks from the database size {} ",stockList.size());
        return stockList;
    }
    public Flux<Stock> streamStocks(){
        return Flux.fromIterable(stockRepository.findAll())
                .delayElements(Duration.ofSeconds(1));
    }

}
