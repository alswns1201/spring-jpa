package com.spring.jpa.stream.repository;

import com.spring.jpa.stream.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends  JpaRepository<Stock, Long> {
}
