package com.yogiBooking.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "nrc_codes")
public class NrcCode extends MasterData{

    @Column(name = "name_mm")
    private String nameMm;
    @Column(name = "name_en")
    private String nameEn;
    @Column
    private int prefixCode;
    @Column
    private String details;

    public String getMmNrcCode(){
        return prefixCode+"/"+nameMm;
    }

    public String getEnNrcCode(){
        return prefixCode+"/"+nameEn;
    }
}
