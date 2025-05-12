package com.yogiBooking.common.dto.yogi;

import com.yogiBooking.common.entity.constants.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MasterYogiDTO {

    private String name;
    private String passportID;
    private String phone;
    private String yogiId;
    private Gender genderType;
    private LocalDate birthDate;
    private Long cityId;
    private Long countryId;
    private Long regionId;
    private String address;
    private String healthStatus;
    private boolean foreignYogi;
    private Long levelId;
    private Long nrcCodeId;

    private String email;
    private String password;
    private Long roleId;

}
