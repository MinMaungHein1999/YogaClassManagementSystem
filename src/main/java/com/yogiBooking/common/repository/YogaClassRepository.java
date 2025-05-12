package com.yogiBooking.common.repository;

import com.yogiBooking.common.dto.CountProjection;
import com.yogiBooking.common.entity.YogaClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface YogaClassRepository extends JpaRepository<YogaClass, Long>, JpaSpecificationExecutor<YogaClass> {

    @Query("""
        SELECT yc.country.id AS countryId, COUNT(yc) AS count
        FROM YogaClass yc
        WHERE yc.classStatus = com.yogiBooking.common.entity.constants.ClassStatus.UPCOMMING
        GROUP BY yc.country.id
    """)
    List<CountProjection> countUpcomingClassesGroupedByCountry();

    @Query("SELECT yc FROM YogaClass yc " +
            "WHERE yc.endDate < :currentDate " +
            "AND yc.classStatus = com.yogiBooking.common.entity.constants.ClassStatus.FINISHED")
    List<YogaClass> findEndedYogaClasses(@Param("currentDate") LocalDate currentDate);

}
