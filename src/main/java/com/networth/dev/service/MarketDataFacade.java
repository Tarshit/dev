package com.networth.dev.service;

import com.networth.dev.model.PortfolioItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MarketDataFacade {

    private final StockService finnhubService;
    private final StockService coinGeckoService;

    public MarketDataFacade(@Qualifier("finnhubStockService") StockService finnhubService,
                            @Qualifier("coinGeckoService") StockService coinGeckoService) {
        this.finnhubService = finnhubService;
        this.coinGeckoService = coinGeckoService;
    }

    public List<PortfolioItem> getCombinedMarketData(String stockSymbols, String cryptoIds) {
        CompletableFuture<List<PortfolioItem>> stocksFuture = CompletableFuture.supplyAsync(() -> {
            if (stockSymbols != null && !stockSymbols.isBlank()) {
                return finnhubService.getStockForSymbol(stockSymbols);
            }
            return Collections.emptyList();
        });

        CompletableFuture<List<PortfolioItem>> cryptoFuture = CompletableFuture.supplyAsync(() -> {
            if (cryptoIds != null && !cryptoIds.isBlank()) {
                return coinGeckoService.getStockForSymbol(cryptoIds);
            }
            return Collections.emptyList();
        });

        List<PortfolioItem> result = new ArrayList<>();
        result.addAll(stocksFuture.join());
        result.addAll(cryptoFuture.join());

        return result;
    }
}