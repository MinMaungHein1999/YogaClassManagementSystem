package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.PackageCreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageCreditHistoryRepository extends JpaRepository<PackageCreditHistory,Long>, JpaSpecificationExecutor<PackageCreditHistory> {

}
