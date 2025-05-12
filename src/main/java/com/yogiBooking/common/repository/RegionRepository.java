package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region,Long>, JpaSpecificationExecutor<Region> {

}
