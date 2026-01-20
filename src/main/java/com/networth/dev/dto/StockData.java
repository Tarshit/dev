package com.networth.dev.dto;

public interface StockData {
    String symbol();
    double price();
    String latestTradingDay();
}