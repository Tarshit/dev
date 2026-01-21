package com.networth.dev.service;

import com.networth.dev.model.PortfolioItem;
import java.util.List;

public interface StockService {
    List<PortfolioItem> getStockForSymbol(String stockSymbol);
}
