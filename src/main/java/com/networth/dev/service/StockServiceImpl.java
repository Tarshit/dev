package com.networth.dev.service;

import com.networth.dev.dto.AlphavantageResponse;
import com.networth.dev.dto.StockResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("alphaVantageStockService")
public class StockServiceImpl implements StockService {

    private final StockClient stockClient;

    public StockServiceImpl(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @Override
    public List<StockResponse> getStockForSymbol(final String stockSymbols) {
        return Arrays.stream(stockSymbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .parallel()
                .map(symbol -> {
                    AlphavantageResponse response = stockClient.getStockQuote(symbol);
                    if (response == null || response.globalQuote() == null || response.globalQuote().price() == null) {
                        throw new IllegalArgumentException("Stock data not found or API limit reached for symbol: " + symbol);
                    }
                    return StockResponse.builder()
                            .symbol(response.globalQuote().symbol())
                            .price(Double.parseDouble(response.globalQuote().price()))
                            .latestTradingDay(response.globalQuote().latestTradingDay())
                            .build();
                })
                .toList();
    }
}