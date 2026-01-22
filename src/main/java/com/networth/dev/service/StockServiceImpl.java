package com.networth.dev.service;

import com.networth.dev.dto.AlphavantageResponse;
import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service("alphaVantageStockService")
public class StockServiceImpl implements StockService {

    private final StockClient stockClient;

    public StockServiceImpl(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @Override
    public List<PortfolioItem> getStockForSymbol(final String stockSymbols) {
        return Arrays.stream(stockSymbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .parallel()
                .map(symbol -> {
                    AlphavantageResponse response = stockClient.getStockQuote(symbol);
                    if (response == null || response.globalQuote() == null || response.globalQuote().price() == null) {
                        throw new IllegalArgumentException("Stock data not found or API limit reached for symbol: " + symbol);
                    }
                    return PortfolioItem.fromMarketData(
                            response.globalQuote().symbol(), // Name (using symbol as name for now)
                            response.globalQuote().symbol(), // Symbol
                            new BigDecimal(response.globalQuote().price()),
                            AssetType.STOCK
                    );
                })
                .toList();
    }
}