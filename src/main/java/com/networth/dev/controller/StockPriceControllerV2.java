package com.networth.dev.controller;

import com.networth.dev.dto.StockResponse;
import com.networth.dev.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/stock-prices")
@Tag(name = "Stock Prices V2 (Finnhub)", description = "Endpoints for retrieving stock market data via Finnhub")
public class StockPriceControllerV2 {

    private final StockService stockService;

    public StockPriceControllerV2(@Qualifier("finnhubStockService") StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    @Operation(summary = "Get Stock Quotes", description = "Retrieves the latest price and trading day for one or more stock symbols (comma-separated) using Finnhub.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stock data")
    @ApiResponse(responseCode = "400", description = "Invalid symbol or API limit reached")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public List<StockResponse> getStock(@Parameter(description = "Comma-separated stock ticker symbols (e.g., AAPL,IBM)", required = true) @PathVariable String symbol){
        return stockService.getStockForSymbol(symbol.toUpperCase());
    }
}