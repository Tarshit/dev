package com.networth.dev.service;

import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("coinGeckoService")
public class CoinGeckoService implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(CoinGeckoService.class);
    private final CoinGeckoClient coinGeckoClient;

    public CoinGeckoService(CoinGeckoClient coinGeckoClient) {
        this.coinGeckoClient = coinGeckoClient;
    }

    @Override
    public List<PortfolioItem> getStockForSymbol(String ids) {
        // CoinGecko expects comma-separated IDs without spaces (e.g., "bitcoin,ethereum")
        // IDs are case-sensitive and usually lowercase.
        String sanitizedIds = ids.replace(" ", "").toLowerCase();
        
        try {
            Map<String, Map<String, Double>> response = coinGeckoClient.getPrices(sanitizedIds);

            if (response == null || response.isEmpty()) {
                logger.warn("Crypto data not found for input: {}", ids);
                return Collections.emptyList();
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
        } catch (Exception e) {
            logger.error("Error fetching crypto data for ids: {}", ids, e);
            return Collections.emptyList();
        }
    }
}