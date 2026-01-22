package com.networth.dev.service;

import com.networth.dev.dto.GoldApiResponse;
import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;

@Service("goldService")
public class GoldService implements StockService {

    private final WebClient webClient;

    public GoldService(@Qualifier("goldApiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<PortfolioItem> getStockForSymbol(String symbols) {
        return Arrays.stream(symbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .parallel()
                .map(symbol -> {
                    GoldApiResponse response;
                    try {
                        response = webClient.get()
                                .uri("/price/{symbol}", symbol)
                                .retrieve()
                                .bodyToMono(GoldApiResponse.class)
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                                .block();
                    } catch (WebClientResponseException.Forbidden e) {
                        return null;
                    } catch (Exception e) {
                        return null;
                    }

                    if (response == null) {
                        return null;
                    }
                    return PortfolioItem.fromMarketData(response.name(), response.symbol(), BigDecimal.valueOf(response.price()), AssetType.METAL);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}