package com.networth.dev.service;

import com.networth.dev.dto.AlphavantageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Service
public class StockClientImpl implements StockClient {

    private final WebClient webClient;
    private final String apiKey;

    public StockClientImpl(@Qualifier("alphaVantageWebClient") WebClient webClient, 
                           @Value("${alpha.vantage.api.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    @Override
    public AlphavantageResponse getStockQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", "{symbol}")
                        .queryParam("apikey", apiKey)
                        .build(symbol))
                .retrieve()
                .bodyToMono(AlphavantageResponse.class)
                .retryWhen(Retry.backoff(3, java.time.Duration.ofSeconds(1)))
                .block();
    }
}