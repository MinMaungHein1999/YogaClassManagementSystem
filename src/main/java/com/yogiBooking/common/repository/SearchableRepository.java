package com.yogiBooking.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchableRepository<T> {
    Page<T> search(String query, Pageable pageable);
}
