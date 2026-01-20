package com.networth.dev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlphavantageResponse(
        @JsonProperty("Global Quote")
        GlobalQuote globalQuote
) {
    public record GlobalQuote(
            @JsonProperty("01. symbol")
            String symbol,
            @JsonProperty("05. price")
            String price,
            @JsonProperty("07. latest trading day")
            String latestTradingDay
    ){}

}
