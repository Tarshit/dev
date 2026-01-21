package com.networth.dev.service;

import java.util.Map;

public interface CoinGeckoClient {
    // Returns a Map where Key = Coin ID (e.g., "bitcoin") and Value = Map of Currency to Price (e.g., "usd" -> 65000.0)
    Map<String, Map<String, Double>> getPrices(String ids);
}