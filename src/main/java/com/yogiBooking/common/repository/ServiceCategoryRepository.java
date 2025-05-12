package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long>, JpaSpecificationExecutor<ServiceCategory> {
    ServiceCategory findByName(String name);
}
