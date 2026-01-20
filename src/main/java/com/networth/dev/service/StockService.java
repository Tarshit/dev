package com.networth.dev.service;

import com.networth.dev.dto.StockResponse;
import java.util.List;

public interface StockService {
    List<StockResponse> getStockForSymbol(String stockSymbol);
}
