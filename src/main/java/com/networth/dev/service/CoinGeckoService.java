package com.networth.dev.service;

import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public List<PortfolioItem> getStockForSymbol(String ids) {
        // CoinGecko expects comma-separated IDs without spaces (e.g., "bitcoin,ethereum")
        // IDs are case-sensitive and usually lowercase.
        String sanitizedIds = ids.replace(" ", "").toLowerCase();
        
        Map<String, Map<String, Double>> response = coinGeckoClient.getPrices(sanitizedIds);

        if (response == null || response.isEmpty()) {
            throw new IllegalArgumentException("Crypto data not found. Ensure you are using CoinGecko IDs (e.g., 'bitcoin', 'binancecoin', 'ripple') not symbols. Input: " + ids);
        }

        List<PortfolioItem> portfolioItems = new ArrayList<>();
        response.forEach((id, data) -> {
            if (data != null && data.containsKey("usd")) {
                portfolioItems.add(PortfolioItem.fromMarketData(
                        id, // name
                        id, // symbol
                        BigDecimal.valueOf(data.get("usd")),
                        AssetType.CRYPTO
                ));
            }
        });
        return portfolioItems;
    }
}