package com.networth.dev.controller;

import com.networth.dev.model.PortfolioItem;
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
@RequestMapping("/v1/stock-prices")
@Tag(name = "Stock Prices V1 (Alpha Vantage)", description = "Endpoints for retrieving stock market data via Alpha Vantage")
public class StockPriceController {

    private final StockService stockService;

    public StockPriceController(@Qualifier("alphaVantageStockService") StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    @Operation(summary = "Get Stock Quotes", description = "Retrieves the latest price and trading day for one or more stock symbols (comma-separated).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stock data")
    @ApiResponse(responseCode = "400", description = "Invalid symbol or API limit reached")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public List<PortfolioItem> getStock(@Parameter(description = "Comma-separated stock ticker symbols (e.g., AAPL,IBM)", required = true) @PathVariable String symbol){
        return stockService.getStockForSymbol(symbol.toUpperCase());
    }
}
