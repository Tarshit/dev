package com.networth.dev.service;

import com.networth.dev.dto.AlphavantageResponse;

public interface StockClient {
    AlphavantageResponse getStockQuote(String symbol);
}
