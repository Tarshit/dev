package com.networth.dev.dto;

import lombok.Builder;

@Builder
public record StockResponse(
        String symbol,
        double price,
        String latestTradingDay
){}

