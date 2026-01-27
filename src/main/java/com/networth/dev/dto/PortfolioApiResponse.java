package com.networth.dev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.networth.dev.model.PortfolioItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioApiResponse {

    private Map<String, String> links;
    private ApiData data;
    private Meta meta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiData {
        private BigDecimal totalInvestment;
        private BigDecimal currentTotalValue;
        private BigDecimal totalProfitPercentage;
        private int totalNumberOfHoldings;
        private String currency;
        private List<PortfolioItemResource> holdings;
        
        @JsonProperty("failedtofetch")
        private List<String> failedToFetch;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioItemResource {
        @Builder.Default
        private String type = "portfolio-item";
        private String id;
        private PortfolioItem attributes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private String timestamp;

    }
}