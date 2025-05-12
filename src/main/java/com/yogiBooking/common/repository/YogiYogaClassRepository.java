package com.yogiBooking.common.repository;

import com.yogiBooking.common.dto.CountProjection;
import com.yogiBooking.common.entity.YogiYogaClass;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface YogiYogaClassRepository extends JpaRepository<YogiYogaClass, Long>, JpaSpecificationExecutor<YogiYogaClass> {
    @Query("""
        SELECT ymc.joinedDate FROM YogiYogaClass ymc
        WHERE ymc.yogi.id = :yogiId AND ymc.yogaClass.id = :classId
    """)
    Optional<LocalDate> findJoinedDateByYogiIdAndClassId(Long yogiId, Long classId);

    @Query("""
        SELECT ymc FROM YogiYogaClass ymc
        WHERE ymc.yogi.id = :yogiId AND ymc.yogaClass.id = :classId
    """)
    Optional<List<YogiYogaClass>> findByYogiIdAndClassId(Long yogiId, Long classId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM YogiYogaClass ymc
        WHERE ymc.yogi.id = :yogiId AND ymc.yogaClass.id = :classId""")
    void deleteByYogiIdAndClassId(@Param("yogiId") Long yogiId, @Param("classId") Long classId);


    @Query("""
        SELECT y.yogi.genderType as key, COUNT(y) as count
        FROM YogiYogaClass y
        WHERE y.yogaClass.id = :classId
        GROUP BY y.yogi.genderType
    """)
    List<CountProjection> countByGender(@Param("classId") Long classId);

    @Query("""
        SELECT y.yogi.level.name as key, COUNT(y) as count
        FROM YogiYogaClass y
        WHERE y.yogaClass.id = :classId
        GROUP BY y.yogi.level.name
    """)
    List<CountProjection> countByLevel(@Param("classId") Long classId);

    @Query("""
        SELECT y.joinedStatus as key, COUNT(y) as count
        FROM YogiYogaClass y
        WHERE y.yogaClass.id = :classId
        GROUP BY y.joinedStatus
    """)
    List<CountProjection> countByJoinedStatus(@Param("classId") Long classId);

    @Query("""
        SELECT y.rating as key, COUNT(y) as count
        FROM YogiYogaClass y
        WHERE y.yogaClass.id = :classId
        GROUP BY y.rating
    """)
    List<CountProjection> countByRating(@Param("classId") Long classId);

    Optional<YogiYogaClass> findByYogiIdAndYogaClassId(Long yogiId, Long yogaClassId);
}
