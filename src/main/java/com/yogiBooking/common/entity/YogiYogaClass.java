package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.JoinedStatusConverter;
import com.yogiBooking.common.converter.PaymentStatusConverter;
import com.yogiBooking.common.converter.RatingConverter;
import com.yogiBooking.common.converter.StatusConverter;
import com.yogiBooking.common.entity.constants.JoinedStatus;
import com.yogiBooking.common.entity.constants.PaymentStatus;
import com.yogiBooking.common.entity.constants.Rating;
import com.yogiBooking.common.entity.constants.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "yogi_yoga_classes")
public class YogiYogaClass extends MasterData{

    @ManyToOne
    @JoinColumn(name = "yoga_class_id")
    private YogaClass yogaClass;

    @ManyToOne
    @JoinColumn(name = "yogi_package_id")
    private YogiPackage yogiPackage;

    @ManyToOne
    @JoinColumn(name = "yogi_id")
    private Yogi yogi;

    @Column
    private LocalDate joinedDate;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status;

    @Column
    @Convert(converter = JoinedStatusConverter.class)
    private JoinedStatus joinedStatus;

    @Column
    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus paymentStatus;

    @Column
    @Convert(converter = RatingConverter.class)
    private Rating rating;

    @Column
    private String remark;
}
