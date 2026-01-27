package com.networth.dev.service;

import com.networth.dev.dto.PortfolioApiResponse;

public interface PortfolioService {
    PortfolioApiResponse getNetWorthByCustomerId(String customerId);
}