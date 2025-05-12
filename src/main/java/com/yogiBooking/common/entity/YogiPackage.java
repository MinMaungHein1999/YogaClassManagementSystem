package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.PackageStatusConverter;
import com.yogiBooking.common.entity.constants.PackageStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@Entity
@Table(name = "yogi_packages")
public class YogiPackage extends MasterData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double credit;

    @ManyToOne
    @JoinColumn(name = "yogi_id")
    private Yogi yogi;

    @Column
    private double amountOfCredit;

    @Column
    private String bankCardNumber;

    @Column
    private LocalDateTime verificationEmailSentAt;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "service_category_id")
    private ServiceCategory serviceCategory;

    @OneToMany(mappedBy = "yogiPackage")
    private List<YogiYogaClass> yogiYogaClasses;

    @Column
    @Convert(converter = PackageStatusConverter.class)
    private PackageStatus packageStatus;
}

