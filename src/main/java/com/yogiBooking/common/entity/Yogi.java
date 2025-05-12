package com.yogiBooking.common.entity;

import com.yogiBooking.common.entity.constants.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "yogis")
public class Yogi extends MasterYogi{

    @Column
    private String yogiId;

    @Column private String passportID;

    @Column
    private Boolean foreignYogi;

    @Column
    private String nrc;

    @Column
    private String phone;

    @Column
    private LocalDate birthDate;

    @OneToMany(mappedBy = "yogi")
    private List<YogiPackage> yogiPackages;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "login_user_id")
    private User loginUser;



    public boolean isForeign(){
        return this.foreignYogi != null && this.foreignYogi;
    }

}
