package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.YogaTeachingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface YogaTeachingServiceRepository extends JpaRepository<YogaTeachingService, Long>, JpaSpecificationExecutor<YogaTeachingService> {

    Optional<List<YogaTeachingService>> findByServiceCategoryId(Long id);
}
