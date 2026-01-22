package com.networth.dev.config;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("alphaVantageWebClient")
    public WebClient alphaVantageWebClient(@Value("${alpha.vantage.base.url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean("finnhubWebClient")
    public WebClient finnhubWebClient(@Value("${finnhub.base.url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean("goldApiWebClient")
    public WebClient goldApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.gold-api.com")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .build();
    }
}
