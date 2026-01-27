package com.networth.dev.controller;

import com.networth.dev.dto.PortfolioApiResponse;
import com.networth.dev.entity.Portfolio;
import com.networth.dev.repository.PortfolioRepository;
import com.networth.dev.service.PortfolioService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/portfolio")
public class PortfolioController {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioRepository portfolioRepository, PortfolioService portfolioService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{customerId}")
    public PortfolioApiResponse getNetWorth(@PathVariable String customerId) {
        return portfolioService.getNetWorthByCustomerId(customerId);
    }
    
    @PutMapping
    public Portfolio updatePortfolio(@RequestBody Portfolio portfolio) {
        return portfolioRepository.findByCustomerId(portfolio.getCustomerId())
                .map(existingPortfolio -> {
                    existingPortfolio.setTotalInvestedAmount(portfolio.getTotalInvestedAmount());
                    existingPortfolio.setTotalAssetsCount(portfolio.getTotalAssetsCount());
                    existingPortfolio.setHoldings(portfolio.getHoldings());
                    existingPortfolio.setCurrency(portfolio.getCurrency());
                    return portfolioRepository.save(existingPortfolio);
                })
                .orElseGet(() -> portfolioRepository.save(portfolio));
    }
}