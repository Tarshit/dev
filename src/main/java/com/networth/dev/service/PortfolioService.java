package com.networth.dev.service;

import com.networth.dev.model.PortfolioItem;
import java.util.List;

public interface PortfolioService {
    List<PortfolioItem> calculatePortfolioValues(List<PortfolioItem> items);
}