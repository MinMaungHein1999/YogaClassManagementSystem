package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.YogiPackage;
import com.yogiBooking.common.entity.constants.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YogiPackageRepository extends JpaRepository<YogiPackage, Long>, JpaSpecificationExecutor<YogiPackage> {

    @Query("SELECT yp FROM YogiPackage yp " +
            "WHERE yp.yogi.id = :yogiId " +
            "AND yp.packageStatus = com.yogiBooking.common.entity.constants.PackageStatus.ACTIVE " +
            "AND EXISTS (SELECT 1 FROM YogiYogaClass yyc " +
            "WHERE yyc.yogiPackage = yp " +
            "AND yyc.yogaClass.id = :classId)")
    Optional<YogiPackage> findActiveByYogiIdAndYogaClassId(@Param("yogiId") Long yogiId, @Param("classId") Long classId);

    @Query("SELECT yp FROM YogiPackage yp " +
            "WHERE yp.country.id = :countryId " +
            "AND yp.yogi.id = :yogiId " +
            "AND yp.serviceCategory.id = :serviceCategoryId " +
            "AND yp.packageStatus = :activeStatus ")
    List<YogiPackage> findActiveByCountryIdAndYogiIdAndServiceCategoryIdAndPackageStatus(
            @Param("countryId") Long countryId,
            @Param("yogiId") Long yogiId,
            @Param("serviceCategoryId") Long serviceCategoryId,
            @Param("activeStatus") PackageStatus activeStatus);
}