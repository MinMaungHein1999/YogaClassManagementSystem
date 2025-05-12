package com.yogiBooking.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yogiBooking.common.entity.YogiNrc;
import com.yogiBooking.common.entity.constants.NrcType;

@Repository
public interface YogiNrcRepository extends JpaRepository<YogiNrc, Long>, JpaSpecificationExecutor<YogiNrc> {
    @Query ("""
    SELECT COUNT(*) > 0 FROM YogiNrc y
    WHERE y.postFixDigit = :postFixDigit
    AND y.type = :type
    AND y.nrcCode.id = :nrcCodeId
   """)
    boolean checkNRCDuplicate(String postFixDigit, NrcType type, Long nrcCodeId);

    @Query("""
    SELECT COUNT(*) > 0 FROM YogiNrc y
    WHERE y.postFixDigit = :postFixDigit
    AND y.type = :type
    AND y.nrcCode.id = :nrcCodeId
    AND y.yogi.id <> :yogiId
    """)
    boolean findYogiNrcByNRCNotID(String postFixDigit, NrcType type, Long nrcCodeId, Long yogiId);

    @Query("""
    SELECT y FROM YogiNrc y
    WHERE y.yogi.id = :yogiId
    """)
    Optional<List<YogiNrc>> findYogiNrcByYogiId(Long yogiId);

    @Query("""
    SELECT COUNT(*) > 0 FROM YogiNrc y
    WHERE y.nrcCode.id = :nrcCodeId
      AND y.type = :type
      AND y.postFixDigit = :postFixDigit
    """)
    boolean isDuplicateNrc(Long nrcCodeId, NrcType type, String postFixDigit);

}
