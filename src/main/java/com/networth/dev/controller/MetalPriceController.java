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
@RequestMapping("/v1/metal-prices")
@Tag(name = "Metal Prices", description = "Endpoints for retrieving metal prices via Gold API")
public class MetalPriceController {

    private final StockService goldService;

    public MetalPriceController(@Qualifier("goldService") StockService goldService) {
        this.goldService = goldService;
    }

    @GetMapping("/{symbol}")
    @Operation(summary = "Get Metal Prices", description = "Retrieves the latest price for one or more metal symbols (comma-separated, e.g., XAU,XAG).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved metal data")
    public List<PortfolioItem> getMetalPrices(@Parameter(description = "Comma-separated metal symbols (e.g., XAU,XAG)", required = true) @PathVariable String symbol){
        return goldService.getStockForSymbol(symbol.toUpperCase());
    }
}