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
@RequestMapping("/v1/crypto-prices")
@Tag(name = "Crypto Prices CoinGecko", description = "Endpoints for retrieving cryptocurrency data via CoinGecko")
public class CryptoPriceController {

    private final StockService stockService;

    public CryptoPriceController(@Qualifier("coinGeckoService") StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{ids}")
    @Operation(summary = "Get Crypto Prices", description = "Retrieves the latest price for one or more cryptocurrency IDs (comma-separated, e.g., bitcoin,ethereum).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved crypto data")
    @ApiResponse(responseCode = "400", description = "Invalid ID or API limit reached")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public List<PortfolioItem> getCryptoPrices(@Parameter(description = "Comma-separated cryptocurrency IDs (e.g., bitcoin,ethereum)", required = true) @PathVariable String ids){
        return stockService.getStockForSymbol(ids);
    }
}