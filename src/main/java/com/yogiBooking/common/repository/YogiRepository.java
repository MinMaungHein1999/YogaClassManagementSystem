package com.yogiBooking.common.repository;

import com.yogiBooking.common.dto.CountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yogiBooking.common.entity.Yogi;

import java.util.List;
import java.util.Optional;

@Repository
public interface YogiRepository extends JpaRepository<Yogi, Long>, JpaSpecificationExecutor<Yogi> {

    @Query("""
        SELECT y.genderType as key, COUNT(y) as count
        FROM Yogi y
        GROUP BY y.genderType
    """)
    List<CountProjection> countByGender();

    @Query("""
        SELECT y.level.name as key, COUNT(y) as count
        FROM Yogi y
        GROUP BY y.level.name
    """)
    List<CountProjection> countByLevel();

    @Query("""
        SELECT CASE WHEN y.foreignYogi = true THEN 'FOREIGN' ELSE 'LOCAL' END as key, COUNT(y) as count
        FROM Yogi y
        GROUP BY y.foreignYogi
    """)
    List<CountProjection> countByForeignStatus();

    @Query("""
        SELECT y FROM Yogi y
        WHERE y.yogiId = :yogiId
        AND y.id <> :id
    """)
    Optional<List<Yogi>> findByYogiIdAndIdNot(String yogiId, Long id);

    @Query("""
        SELECT y FROM Yogi y
        LEFT JOIN YogiYogaClass
        ymc ON y.id = ymc.yogi.id
        AND ymc.yogaClass.id = :classId
        WHERE ymc.id IS NULL
    """)
    Optional<List<Yogi>> findYogisNotInYogaClass(@Param("classId") Long classId);

    @Query("""
        SELECT y FROM Yogi y
        WHERE y.yogiId = :yogiId
    """)
    Optional<List<Yogi>> findByYogiId(String yogiId);

    @Query("""
        SELECT y FROM Yogi y
        WHERE y.passportID = :passportID
    """)
    Optional<List<Yogi>> findByPassportId(String passportID);


    @Query("""
        SELECT y FROM Yogi y
        WHERE y.passportID = :passportID
        AND y.id <> :id
    """)
    Optional<List<Yogi>> findByPassportIdAndIdNot(String passportID, Long id);

    List<Yogi> findByIdIn(List<Long> ids);
}
