package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.GenderConverter;
import com.yogiBooking.common.converter.StatusConverter;
import com.yogiBooking.common.entity.constants.Gender;
import com.yogiBooking.common.entity.constants.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class MasterYogi extends MasterData{

    @Column
    private String name;

    @Column
    private String address;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status;

    @Convert(converter = GenderConverter.class)
    @Column(name = "gender")
    private Gender genderType;
}
