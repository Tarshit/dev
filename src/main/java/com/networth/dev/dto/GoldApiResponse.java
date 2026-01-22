package com.networth.dev.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoldApiResponse(String name, double price, String symbol, String updatedAt) {
}