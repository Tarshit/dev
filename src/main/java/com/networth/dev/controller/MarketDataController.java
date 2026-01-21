package com.networth.dev.controller;

import com.networth.dev.model.PortfolioItem;
import com.networth.dev.service.MarketDataFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/market-data")
@Tag(name = "Unified Market Data", description = "Endpoints for retrieving combined stock and crypto data")
public class MarketDataController {

    private final MarketDataFacade marketDataFacade;

    public MarketDataController(MarketDataFacade marketDataFacade) {
        this.marketDataFacade = marketDataFacade;
    }

    @GetMapping("/prices")
    @Operation(summary = "Get Combined Prices", description = "Get prices for both stocks and cryptos in a single call.")
    public List<PortfolioItem> getPrices(
            @Parameter(description = "Comma-separated stock symbols (e.g. AAPL, TSLA)") @RequestParam(required = false) String stocks,
            @Parameter(description = "Comma-separated crypto IDs (e.g. bitcoin, ethereum)") @RequestParam(required = false) String cryptos) {
        return marketDataFacade.getCombinedMarketData(stocks, cryptos);
    }
}