package com.networth.dev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import com.networth.dev.model.PortfolioItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "holdings")
public class Portfolio {
    @Id
    private String id;
    @Indexed(unique = true)
    private String customerId;
    private String currency;
    private Double totalInvestedAmount;
    private Integer totalAssetsCount;
    private List<PortfolioItem> holdings;
}