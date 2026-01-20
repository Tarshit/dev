package com.networth.dev.service;

import com.networth.dev.dto.FinnhubResponse;

public interface FinnhubClient {
    FinnhubResponse getQuote(String symbol);
}