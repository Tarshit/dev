package com.networth.dev.controller;

import com.networth.dev.dto.StockResponse;
import com.networth.dev.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock-prices")
public class StockPriceController {

    private final StockService stockService;

    public StockPriceController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    public StockResponse getStock(@PathVariable String symbol){
        return stockService.getStockForSymbol(symbol.toUpperCase());
    }
}
