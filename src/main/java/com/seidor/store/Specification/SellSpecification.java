package com.seidor.store.Specification;

import com.seidor.store.model.Sell;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SellSpecification {

    public static Specification<Sell> createdAfter(LocalDateTime startDate) {
        return (root, query, builder) -> startDate == null ? null : builder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<Sell> createdBefore(LocalDateTime endDate) {
        return (root, query, builder) -> endDate == null ? null : builder.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }

    public static Specification<Sell> hasUserId(Integer userId) {
        return (root, query, builder) -> userId == null ? null : builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Sell> totalSaleGreaterThanOrEqual(Double minTotalSale) {
        return (root, query, builder) -> minTotalSale == null ? null : builder.greaterThanOrEqualTo(root.get("totalSale"), minTotalSale);
    }

    public static Specification<Sell> totalSaleLessThanOrEqual(Double maxTotalSale) {
        return (root, query, builder) -> maxTotalSale == null ? null : builder.lessThanOrEqualTo(root.get("totalSale"), maxTotalSale);
    }
}
