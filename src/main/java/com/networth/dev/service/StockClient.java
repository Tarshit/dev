package com.networth.dev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.networth.dev.dto.AlphavantageResponse;


@Service
public class StockClient {

    private final WebClient webClient;
    private final String apiKey;

    public StockClient(WebClient webClient, @Value("${alpha.vantage.api.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    public AlphavantageResponse getStockQuote(String symbol){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", "{symbol}")
                        .queryParam("apikey", apiKey)
                        .build(symbol))
                .retrieve()
                .bodyToMono(AlphavantageResponse.class)
                .block();
    }
}
