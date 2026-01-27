package com.networth.dev.repository;

import com.networth.dev.entity.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PortfolioRepository extends MongoRepository<Portfolio, String> {
    Optional<Portfolio> findByCustomerId(String customerId);
}