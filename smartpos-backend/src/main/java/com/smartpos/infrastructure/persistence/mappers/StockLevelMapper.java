package com.smartpos.infrastructure.persistence.mappers;

import com.smartpos.domain.models.StockLevelDomain;
import com.smartpos.infrastructure.persistence.entities.StockLevelJPA;

/**
 * Mapper to convert between:
 * - Domain Model (business logic, no persistence concerns)
 * - JPA Entity (database mapping, persistence concerns)
 */
public class StockLevelMapper {
    
    /**
     * Convert JPA Entity → Domain Model
     * Used when loading from database
     */
    public static StockLevelDomain toDomain(StockLevelJPA jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return new StockLevelDomain(
            jpaEntity.getId(),
            jpaEntity.getProductId(),
            jpaEntity.getMinimumLevel(),
            jpaEntity.getMaximumLevel()
        );
    }
    
    /**
     * Convert Domain Model → JPA Entity
     * Used when saving to database
     */
    public static StockLevelJPA toJPA(StockLevelDomain domain) {
        if (domain == null) {
            return null;
        }
        return StockLevelJPA.builder()
            .id(domain.getId())
            .productId(domain.getProductId())
            .minimumLevel(domain.getMinimumLevel())
            .maximumLevel(domain.getMaximumLevel())
            .build();
    }
}
