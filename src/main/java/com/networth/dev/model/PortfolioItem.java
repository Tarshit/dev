package com.networth.dev.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private BigDecimal quantity = BigDecimal.ZERO;

    @JsonProperty("weightInOz")
    private BigDecimal weightInOz = BigDecimal.ZERO;

    private BigDecimal averageBuyPrice = BigDecimal.ZERO;
    private BigDecimal currentPrice;
    @NonNull
    private AssetType assetType;
    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currency;
    private LocalDateTime lastUpdated;

    @JsonProperty("currentvalue")
    private BigDecimal currentValue = BigDecimal.ZERO;

    @JsonProperty("profitpercentage")
    private BigDecimal profitPercentage = BigDecimal.ZERO;

    @JsonProperty("totalInvestment")
    private BigDecimal totalInvestment = BigDecimal.ZERO;

    public PortfolioItem() {
    }

    public PortfolioItem(String name, String symbol, String currency, AssetType assetType, BigDecimal quantity, BigDecimal averageBuyPrice) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.symbol = Objects.requireNonNull(symbol, "Symbol cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.assetType = Objects.requireNonNull(assetType, "AssetType cannot be null");
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
        this.lastUpdated = LocalDateTime.now();
    }

    public static PortfolioItem fromMarketData(String name, String symbol, BigDecimal currentPrice, AssetType assetType) {
        PortfolioItem item = new PortfolioItem(name, symbol, "USD", assetType, BigDecimal.ZERO, BigDecimal.ZERO);
        item.setCurrentPrice(currentPrice);
        return item;
    }
}