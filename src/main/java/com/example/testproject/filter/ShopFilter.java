package com.example.testproject.filter;

import com.example.testproject.entity.Shop;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public record ShopFilter(String addressContains, Integer phoneGte, Integer phoneLte) {
    public Specification<Shop> toSpecification() {
        return Specification.where(addressContainsSpec())
                .and(phoneGteSpec())
                .and(phoneLteSpec());
    }

    private Specification<Shop> addressContainsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(addressContains)
                ? cb.like(cb.lower(root.get("address")), "%" + addressContains.toLowerCase() + "%")
                : null);
    }

    private Specification<Shop> phoneGteSpec() {
        return ((root, query, cb) -> phoneGte != null
                ? cb.greaterThanOrEqualTo(root.get("phone"), phoneGte)
                : null);
    }

    private Specification<Shop> phoneLteSpec() {
        return ((root, query, cb) -> phoneLte != null
                ? cb.lessThanOrEqualTo(root.get("phone"), phoneLte)
                : null);
    }
}