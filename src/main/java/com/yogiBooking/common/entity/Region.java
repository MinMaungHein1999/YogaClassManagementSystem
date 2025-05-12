package com.yogiBooking.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@Table(name = "regions")
public class Region extends MasterData {

    @Column
    public String nameEn;

    @Column
    private String nameMm;

    @Column
    private String code;

    @OneToMany(mappedBy = "region",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<City> cities = new HashSet<>();

    @OneToMany(mappedBy = "region",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Yogi> yogis = new HashSet<>();

}
