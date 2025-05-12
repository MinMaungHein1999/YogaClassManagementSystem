package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CityRepository extends JpaRepository<City,Long>, JpaSpecificationExecutor<City> {
}
