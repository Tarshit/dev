package com.networth.dev.service;

import com.networth.dev.dto.AlphavantageResponse;
import com.networth.dev.dto.StockResponse;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockClient stockClient;

    public StockService(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    public StockResponse getStockForSymbol(final String stockSymbol) {
       final AlphavantageResponse response = stockClient.getStockQuote(stockSymbol);

       if (response == null || response.globalQuote() == null || response.globalQuote().price() == null) {
           throw new IllegalArgumentException("Stock data not found or API limit reached for symbol: " + stockSymbol);
       }

        return StockResponse.builder()
                .symbol(response.globalQuote().symbol())
                .price(Double.parseDouble(response.globalQuote().price()))
                .latestTradingDay(response.globalQuote().latestTradingDay())
                .build();
    }

}
