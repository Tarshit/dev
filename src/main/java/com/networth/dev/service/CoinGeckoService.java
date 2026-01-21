package com.networth.dev.service;

import com.networth.dev.dto.StockResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("coinGeckoService")
public class CoinGeckoService implements StockService {

    private final CoinGeckoClient coinGeckoClient;

    public CoinGeckoService(CoinGeckoClient coinGeckoClient) {
        this.coinGeckoClient = coinGeckoClient;
    }

    @Override
    public List<StockResponse> getStockForSymbol(String ids) {
        // CoinGecko expects comma-separated IDs without spaces (e.g., "bitcoin,ethereum")
        // IDs are case-sensitive and usually lowercase.
        String sanitizedIds = ids.replace(" ", "").toLowerCase();
        
        Map<String, Map<String, Double>> response = coinGeckoClient.getPrices(sanitizedIds);

        if (response == null || response.isEmpty()) {
            throw new IllegalArgumentException("Crypto data not found. Ensure you are using CoinGecko IDs (e.g., 'bitcoin', 'binancecoin', 'ripple') not symbols. Input: " + ids);
        }

        List<StockResponse> stockResponses = new ArrayList<>();
        response.forEach((id, data) -> {
            if (data != null && data.containsKey("usd")) {
                stockResponses.add(StockResponse.builder()
                        .symbol(id) // CoinGecko returns the ID (e.g., "bitcoin") as the key
                        .price(data.get("usd"))
                        .latestTradingDay(LocalDate.now().toString())
                        .build());
            }
        });
        return stockResponses;
    }
}