package com.networth.dev.service;

import com.networth.dev.dto.PortfolioApiResponse;
import com.networth.dev.entity.Portfolio;
import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import com.networth.dev.repository.PortfolioRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final MarketDataFacade marketDataFacade;
    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(MarketDataFacade marketDataFacade, PortfolioRepository portfolioRepository) {
        this.marketDataFacade = marketDataFacade;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public PortfolioApiResponse getNetWorthByCustomerId(String customerId) {
        List<PortfolioItem> items = portfolioRepository.findByCustomerId(customerId)
                .map(Portfolio::getHoldings)
                .orElse(Collections.emptyList());
        return calculatePortfolioValues(items, "/v1/portfolio/" + customerId);
    }

    private PortfolioApiResponse calculatePortfolioValues(List<PortfolioItem> items, String selfLink) {
        if (items == null || items.isEmpty()) {
            return createEmptyResponse(selfLink);
        }

        List<PortfolioItem> uniqueItems = getPortfolioItems(items);

        String stockSymbols = uniqueItems.stream()
                .filter(i -> i.getAssetType() == AssetType.STOCK)
                .map(PortfolioItem::getSymbol)
                .collect(Collectors.joining(","));

        String cryptoIds = uniqueItems.stream()
                .filter(i -> i.getAssetType() == AssetType.CRYPTO)
                .map(PortfolioItem::getSymbol)
                .collect(Collectors.joining(","));

        String metalSymbols = uniqueItems.stream()
                .filter(i -> i.getAssetType() == AssetType.METAL)
                .map(PortfolioItem::getSymbol)
                .collect(Collectors.joining(","));

        List<PortfolioItem> marketPrices = marketDataFacade.getCombinedMarketData(stockSymbols, cryptoIds, metalSymbols);

        Map<String, BigDecimal> priceMap = marketPrices.stream()
                .collect(Collectors.toMap(
                        i -> i.getSymbol().toLowerCase(),
                        PortfolioItem::getCurrentPrice,
                        (p1, p2) -> p1
                ));

        List<PortfolioApiResponse.FailedItem> failedToFetch = new ArrayList<>();
        List<PortfolioItem> validItems = new ArrayList<>();

        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        BigDecimal totalPortfolioInvestment = BigDecimal.ZERO;

        for (PortfolioItem item : uniqueItems) {
            BigDecimal currentPrice = priceMap.getOrDefault(item.getSymbol().toLowerCase(), BigDecimal.ZERO);
            
            if (currentPrice.compareTo(BigDecimal.ZERO) == 0) {
                failedToFetch.add(PortfolioApiResponse.FailedItem.builder()
                        .symbol(item.getSymbol())
                        .name(item.getName())
                        .build());
                continue;
            }
            
            item.setCurrentPrice(currentPrice);
            item.setLastUpdated(LocalDateTime.now());

            // Use weightInOz for METAL, quantity for others
            BigDecimal quantityFactor = (item.getAssetType() == AssetType.METAL) ? item.getWeightInOz() : item.getQuantity();
            if (quantityFactor == null) quantityFactor = BigDecimal.ZERO;
            item.setCurrentValue(quantityFactor.multiply(currentPrice));
            item.setTotalInvestment(quantityFactor.multiply(item.getAverageBuyPrice()));

            totalPortfolioValue = totalPortfolioValue.add(item.getCurrentValue());
            totalPortfolioInvestment = totalPortfolioInvestment.add(item.getTotalInvestment());

            if (item.getAverageBuyPrice().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal diff = currentPrice.subtract(item.getAverageBuyPrice());
                item.setProfitPercentage(diff.divide(item.getAverageBuyPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            }

            validItems.add(item);
        }

        BigDecimal totalProfitPercentage = BigDecimal.ZERO;
        if (totalPortfolioInvestment.compareTo(BigDecimal.ZERO) != 0) {
            totalProfitPercentage = totalPortfolioValue.subtract(totalPortfolioInvestment)
                    .divide(totalPortfolioInvestment, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return buildResponse(validItems, failedToFetch, selfLink, totalPortfolioValue, totalPortfolioInvestment, totalProfitPercentage);
    }

    private PortfolioApiResponse buildResponse(List<PortfolioItem> items, List<PortfolioApiResponse.FailedItem> failedToFetch, String selfLink, BigDecimal totalValue, BigDecimal totalInvestment, BigDecimal totalProfitPercentage) {
        List<PortfolioApiResponse.PortfolioItemResource> resources = items.stream()
                .map(item -> PortfolioApiResponse.PortfolioItemResource.builder()
                        .type("portfolio-item")
                        .id(item.getSymbol())
                        .attributes(item)
                        .build())
                .toList();

        PortfolioApiResponse.ApiData data = PortfolioApiResponse.ApiData.builder()
                .holdings(resources)
                .failedToFetch(failedToFetch)
                .totalInvestment(totalInvestment)
                .currentTotalValue(totalValue)
                .totalProfitPercentage(totalProfitPercentage)
                .totalNumberOfHoldings(items.size())
                .currency("USD")
                .build();

        PortfolioApiResponse.Meta meta = PortfolioApiResponse.Meta.builder()
                .timestamp(DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()))
                .build();

        return PortfolioApiResponse.builder()
                .links(Map.of("self", selfLink))
                .data(data)
                .meta(meta)
                .build();
    }

    private PortfolioApiResponse createEmptyResponse(String selfLink) {
        return buildResponse(Collections.emptyList(), Collections.emptyList(), selfLink, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private static @NonNull List<PortfolioItem> getPortfolioItems(List<PortfolioItem> items) {
        Map<String, PortfolioItem> aggregatedMap = new HashMap<>();

        for (PortfolioItem item : items) {
            String key = item.getSymbol().toLowerCase() + "-" + item.getAssetType();

            aggregatedMap.compute(key, (k, existing) -> {
                if (existing == null) {
                    return item;
                }

                BigDecimal qty1 = getOrDefault(existing.getQuantity());
                BigDecimal weight1 = getOrDefault(existing.getWeightInOz());
                BigDecimal price1 = getOrDefault(existing.getAverageBuyPrice());

                BigDecimal qty2 = getOrDefault(item.getQuantity());
                BigDecimal weight2 = getOrDefault(item.getWeightInOz());
                BigDecimal price2 = getOrDefault(item.getAverageBuyPrice());

                BigDecimal totalQty = qty1.add(qty2);
                BigDecimal totalWeight = weight1.add(weight2);


                BigDecimal metric1 = (existing.getAssetType() == AssetType.METAL) ? weight1 : qty1;
                BigDecimal metric2 = (item.getAssetType() == AssetType.METAL) ? weight2 : qty2;
                BigDecimal totalMetric = (existing.getAssetType() == AssetType.METAL) ? totalWeight : totalQty;

                BigDecimal cost1 = metric1.multiply(price1);
                BigDecimal cost2 = metric2.multiply(price2);
                BigDecimal totalCost = cost1.add(cost2);

                BigDecimal newAvgPrice = BigDecimal.ZERO;
                if (totalMetric.compareTo(BigDecimal.ZERO) != 0) {
                    newAvgPrice = totalCost.divide(totalMetric, 4, RoundingMode.HALF_UP);
                }

                existing.setQuantity(totalQty);
                existing.setWeightInOz(totalWeight);
                existing.setAverageBuyPrice(newAvgPrice);
                return existing;
            });
        }

        List<PortfolioItem> uniqueItems = new ArrayList<>(aggregatedMap.values());
        return uniqueItems;
    }

    private static BigDecimal getOrDefault(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}