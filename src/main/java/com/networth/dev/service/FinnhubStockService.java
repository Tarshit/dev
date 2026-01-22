package com.networth.dev.service;

import com.networth.dev.dto.FinnhubResponse;
import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service("finnhubStockService")
public class FinnhubStockService implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(FinnhubStockService.class);
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
                    try {
                        FinnhubResponse response = finnhubClient.getQuote(symbol);
                        if (response == null || response.c() == 0) {
                            logger.warn("Stock data not found or invalid for symbol: {}", symbol);
                            return null;
                        }
                        return PortfolioItem.fromMarketData(
                                symbol.toUpperCase(), // Name
                                symbol.toUpperCase(), // Symbol
                                BigDecimal.valueOf(response.c()),
                                AssetType.STOCK
                        );
                    } catch (Exception e) {
                        logger.error("Error fetching stock data for symbol: {}", symbol, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}