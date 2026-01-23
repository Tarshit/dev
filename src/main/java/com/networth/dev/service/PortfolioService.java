package com.networth.dev.service;

import com.networth.dev.dto.PortfolioApiResponse;
import com.networth.dev.model.PortfolioItem;
import java.util.List;

public interface PortfolioService {
    PortfolioApiResponse calculatePortfolioValues(List<PortfolioItem> items);
}