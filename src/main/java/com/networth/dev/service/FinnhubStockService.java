package com.networth.dev.service;

import com.networth.dev.dto.FinnhubResponse;
import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service("finnhubStockService")
public class FinnhubStockService implements StockService {

    private final FinnhubClient finnhubClient;

    public FinnhubStockService(FinnhubClient finnhubClient) {
        this.finnhubClient = finnhubClient;
    }

    @Override
    public List<PortfolioItem> getStockForSymbol(String stockSymbols) {
        return Arrays.stream(stockSymbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .parallel()
                .map(symbol -> {
                    FinnhubResponse response = finnhubClient.getQuote(symbol);
                    if (response == null || response.c() == 0) {
                        throw new IllegalArgumentException("Stock data not found for symbol: " + symbol);
                    }
                    return PortfolioItem.fromMarketData(
                            symbol.toUpperCase(), // Name
                            symbol.toUpperCase(), // Symbol
                            BigDecimal.valueOf(response.c()),
                            AssetType.STOCK
                    );
                })
                .toList();
    }
}