package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.StatusConverter;
import com.yogiBooking.common.entity.constants.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "yoga_teaching_services")
public class YogaTeachingService extends MasterData {

    @Column
    private String name;
    @Column
    private String code;
    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "service_category_id")
    private ServiceCategory serviceCategory;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status;
}
