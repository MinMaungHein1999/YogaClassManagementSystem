package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.ClassStatusConverter;
import com.yogiBooking.common.converter.ClassTypeConverter;
import com.yogiBooking.common.entity.constants.ClassStatus;
import com.yogiBooking.common.entity.constants.ClassType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "yoga_classes")
public class YogaClass extends MasterData{

    @Column
    private String title;

    @Column
    private Long maxNumberOfYogis;

    @Column
    private Double feeOfCredit;

    @Column
    private Double feeOfPrice;

    @Column
    private String description;

    @Convert(converter = ClassStatusConverter.class)
    @Column(name = "class_status")
    private ClassStatus classStatus;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private String address;

    @Convert(converter = ClassTypeConverter.class)
    @Column(name = "class_type")
    private ClassType classType;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "service_category_id", nullable = true)
    private ServiceCategory serviceCategory;

    @ManyToOne
    @JoinColumn(name = "yoga_teaching_service_id", nullable = false)
    private YogaTeachingService yogaTeachingService;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

}
