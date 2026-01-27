package com.networth.dev.entity;

import com.networth.dev.model.AssetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoldingItem {
    private String name;
    private String symbol;
    private double quantity;
    private AssetType assetType;
    private double averageBuyPrice;
    private double weightInOz;
}