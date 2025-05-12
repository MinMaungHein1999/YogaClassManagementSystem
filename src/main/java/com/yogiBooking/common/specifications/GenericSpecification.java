package com.yogiBooking.common.specifications;

import com.yogiBooking.common.dto.SearchFilter;
import com.yogiBooking.common.entity.YogiYogaClass;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {
    private final List<SearchFilter> filters;

    public GenericSpecification(List<SearchFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        for (SearchFilter filter : filters) {
            if (filter.getValue() == null || filter.getValue().isEmpty()) continue;

            if (filter.getTerm().equals("notInClassId")) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<YogiYogaClass> ymcRoot = subquery.from(YogiYogaClass.class);
                subquery.select(ymcRoot.get("yogi").get("id"))
                        .where(cb.equal(ymcRoot.get("yogaClass").get("id"), Long.parseLong(filter.getValue())));
                predicate = cb.and(predicate, cb.not(root.get("id").in(subquery)));
                continue;
            }

            // Handle "inClassId"
            if (filter.getTerm().equals("inClassId")) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<YogiYogaClass> ymcRoot = subquery.from(YogiYogaClass.class);
                subquery.select(ymcRoot.get("yogi").get("id"))
                        .where(cb.equal(ymcRoot.get("yogaClass").get("id"), Long.parseLong(filter.getValue())));
                predicate = cb.and(predicate, root.get("id").in(subquery));
                continue;
            }

            Path<?> fieldPath;
            if (filter.getTerm().contains(".")) {
                String[] parts = filter.getTerm().split("\\.");
                Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                fieldPath = join.get(parts[1]);
            } else {
                fieldPath = root.get(filter.getTerm());
            }

            switch (filter.getMatchType()) {
                case "contains":
                    predicate = cb.and(predicate, cb.like(cb.lower(fieldPath.as(String.class)), "%" + filter.getValue().toLowerCase() + "%"));
                    break;
                case "exact":
                    if (filter.getType().equalsIgnoreCase("boolean")) {
                        boolean boolValue = Boolean.parseBoolean(filter.getValue());
                        predicate = cb.and(predicate, cb.equal(fieldPath.as(Boolean.class), boolValue));
                    } else if (filter.getType().equalsIgnoreCase("enum") || fieldPath.getJavaType().isEnum()) {
                        Object enumValue = Enum.valueOf((Class<Enum>) fieldPath.getJavaType(), filter.getValue());
                        predicate = cb.and(predicate, cb.equal(fieldPath, enumValue));
                    } else if (filter.getType().equalsIgnoreCase("number")) {
                        predicate = cb.and(predicate, cb.equal(fieldPath.as(Integer.class), Integer.parseInt(filter.getValue())));
                    } else {
                        predicate = cb.and(predicate, cb.equal(fieldPath, filter.getValue()));
                    }
                    break;
                case "less_than_equal":
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(fieldPath.as(Integer.class), Integer.parseInt(filter.getValue())));
                    break;
                case "greater_than_equal":
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(fieldPath.as(Integer.class), Integer.parseInt(filter.getValue())));
                    break;
            }
        }

        return predicate;
    }
}

