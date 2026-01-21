package com.networth.dev.service;

import com.networth.dev.dto.FinnhubResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Service
public class FinnhubClientImpl implements FinnhubClient {

    private final WebClient webClient;
    private final String apiKey;

    public FinnhubClientImpl(@Qualifier("finnhubWebClient") WebClient webClient,
                             @Value("${finnhub.api.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    @Override
    public FinnhubResponse getQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubResponse.class)
                .retryWhen(Retry.backoff(3, java.time.Duration.ofSeconds(1)))
                .block();
    }
}