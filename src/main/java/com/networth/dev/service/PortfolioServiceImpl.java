package com.networth.dev.service;

import com.networth.dev.model.AssetType;
import com.networth.dev.model.PortfolioItem;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final MarketDataFacade marketDataFacade;

    public PortfolioServiceImpl(MarketDataFacade marketDataFacade) {
        this.marketDataFacade = marketDataFacade;
    }

    @Override
    public List<PortfolioItem> calculatePortfolioValues(List<PortfolioItem> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
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

        for (PortfolioItem item : uniqueItems) {
            BigDecimal currentPrice = priceMap.getOrDefault(item.getSymbol().toLowerCase(), BigDecimal.ZERO);
            item.setCurrentPrice(currentPrice);
            item.setLastUpdated(LocalDateTime.now());

            // Use weightInOz for METAL, quantity for others
            BigDecimal quantityFactor = (item.getAssetType() == AssetType.METAL) ? item.getWeightInOz() : item.getQuantity();
            if (quantityFactor == null) quantityFactor = BigDecimal.ZERO;
            item.setCurrentValue(quantityFactor.multiply(currentPrice));

            if (item.getAverageBuyPrice().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal diff = currentPrice.subtract(item.getAverageBuyPrice());
                item.setProfitPercentage(diff.divide(item.getAverageBuyPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            }
        }

        return uniqueItems;
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