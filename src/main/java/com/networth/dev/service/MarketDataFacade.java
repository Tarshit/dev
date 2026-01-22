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
    private final StockService goldService;

    public MarketDataFacade(@Qualifier("finnhubStockService") StockService finnhubService,
                            @Qualifier("coinGeckoService") StockService coinGeckoService,
                            @Qualifier("goldService") StockService goldService) {
        this.finnhubService = finnhubService;
        this.coinGeckoService = coinGeckoService;
        this.goldService = goldService;
    }

    public List<PortfolioItem> getCombinedMarketData(String stockSymbols, String cryptoIds, String metalSymbols) {
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

        CompletableFuture<List<PortfolioItem>> metalsFuture = CompletableFuture.supplyAsync(() -> {
            if (metalSymbols != null && !metalSymbols.isBlank()) {
                return goldService.getStockForSymbol(metalSymbols);
            }
            return Collections.emptyList();
        });

        List<PortfolioItem> result = new ArrayList<>();
        result.addAll(stocksFuture.join());
        result.addAll(cryptoFuture.join());
        result.addAll(metalsFuture.join());

        return result;
    }

    public List<PortfolioItem> getCombinedMarketData(String stockSymbols, String cryptoIds) {
        return getCombinedMarketData(stockSymbols, cryptoIds, null);
    }
}