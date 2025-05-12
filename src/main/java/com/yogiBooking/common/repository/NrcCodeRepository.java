package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.NrcCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface NrcCodeRepository extends JpaRepository<NrcCode, Long>,
        JpaSpecificationExecutor<NrcCode> {

    NrcCode findByNameMmAndNameEnAndPrefixCode(String nameMM, String nameEN, int prefixCode);

}