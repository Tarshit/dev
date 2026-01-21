package com.networth.dev.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class CoinGeckoClientImpl implements CoinGeckoClient {

    private final WebClient webClient;
    private final String apiKey;

    public CoinGeckoClientImpl(@Qualifier("coinGeckoWebClient") WebClient webClient,
                               @Value("${coingecko.api.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    @Override
    public Map<String, Map<String, Double>> getPrices(String ids) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/simple/price")
                        .queryParam("ids", ids)
                        .queryParam("vs_currencies", "usd")
                        .build())
                .header("x-cg-demo-api-key", apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .block();
    }
}