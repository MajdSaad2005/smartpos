package com.smartpos.application.mappers;

import com.smartpos.application.dtos.StockLevelDTO;
import com.smartpos.domain.models.StockLevelDomain;

/**
 * Maps between Domain models and DTOs for StockLevel.
 */
public class StockLevelMapper {

    public static StockLevelDTO toDTO(StockLevelDomain domain, String productName) {
        if (domain == null) return null;
        return StockLevelDTO.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .productName(productName)
                .minimumLevel(domain.getMinimumLevel())
                .maximumLevel(domain.getMaximumLevel())
                .build();
    }

    public static StockLevelDomain fromDTO(StockLevelDTO dto) {
        if (dto == null) return null;
        return new StockLevelDomain(
                dto.getId(),
                dto.getProductId(),
                dto.getMinimumLevel(),
                dto.getMaximumLevel()
        );
    }
}
