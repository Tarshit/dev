package com.networth.dev.config;

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
}
