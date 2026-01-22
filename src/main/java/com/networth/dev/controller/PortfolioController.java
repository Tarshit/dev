package com.networth.dev.controller;

import com.networth.dev.model.PortfolioItem;
import com.networth.dev.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/portfolio")
@Tag(name = "Portfolio Management", description = "Endpoints for portfolio calculations")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate Portfolio Value", description = "Calculates current value and profit percentage for a list of stocks/cryptos. Aggregates duplicate symbols.")
    public List<PortfolioItem> calculatePortfolio(@RequestBody List<PortfolioItem> portfolio) {
        return portfolioService.calculatePortfolioValues(portfolio);
    }
}