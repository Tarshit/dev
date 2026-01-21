package com.networth.dev.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Data;
import lombok.NonNull;

@Data
public class PortfolioItem {

    @NonNull
    private String name;
    @NonNull
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
    @NonNull
    private AssetType assetType;
    @NonNull
    private String currency;
    private LocalDateTime lastUpdated;

    public PortfolioItem(String name, String symbol, String currency, AssetType assetType, BigDecimal quantity, BigDecimal averageBuyPrice) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.symbol = Objects.requireNonNull(symbol, "Symbol cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.assetType = Objects.requireNonNull(assetType, "AssetType cannot be null");
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Factory method to create a PortfolioItem representing market data (quote).
     * Quantity and AverageBuyPrice are set to zero.
     */
    public static PortfolioItem fromMarketData(String name, String symbol, BigDecimal currentPrice, AssetType assetType) {
        PortfolioItem item = new PortfolioItem(name, symbol, "USD", assetType, BigDecimal.ZERO, BigDecimal.ZERO);
        item.setCurrentPrice(currentPrice);
        return item;
    }
}