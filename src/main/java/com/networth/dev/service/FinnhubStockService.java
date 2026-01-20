package com.networth.dev.service;

import com.networth.dev.dto.FinnhubResponse;
import com.networth.dev.dto.StockResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service("finnhubStockService")
public class FinnhubStockService implements StockService {

    private final FinnhubClient finnhubClient;

    public FinnhubStockService(FinnhubClient finnhubClient) {
        this.finnhubClient = finnhubClient;
    }

    @Override
    public List<StockResponse> getStockForSymbol(String stockSymbols) {
        return Arrays.stream(stockSymbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .parallel()
                .map(symbol -> {
                    FinnhubResponse response = finnhubClient.getQuote(symbol);
                    if (response == null || response.c() == 0) {
                        throw new IllegalArgumentException("Stock data not found for symbol: " + symbol);
                    }
                    return StockResponse.builder()
                            .symbol(symbol.toUpperCase())
                            .price(response.c())
                            .latestTradingDay(Instant.ofEpochSecond(response.t()).atZone(ZoneId.of("America/New_York")).toLocalDate().toString())
                            .build();
                })
                .toList();
    }
}