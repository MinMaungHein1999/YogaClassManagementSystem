package com.yogiBooking.common.service;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.specifications.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenericSearchService{
    public <T> Page<T> search(SearchRequest request, JpaSpecificationExecutor<T> repository, Pageable pageable) {
        Specification<T> spec = new GenericSpecification<>(request.getFilters());
        return repository.findAll(spec, pageable);
    }

    /**
     * Search all records without pagination
     * @param request SearchRequest
     * @param repository JpaSpecificationExecutor
     * @return List<T>
     * @param <T> Entity type
     */
    public <T> List<T> searchAll(SearchRequest request, JpaSpecificationExecutor<T> repository) {
        Specification<T> spec = new GenericSpecification<>(request.getFilters());
        return repository.findAll(spec);
    }
}

