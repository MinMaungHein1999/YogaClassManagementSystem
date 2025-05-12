package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.NrcTypeConverter;
import com.yogiBooking.common.entity.constants.NrcType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "yogi_nrcs")
public class YogiNrc extends MasterData{

    @Column
    private String postFixDigit;

    @Column
    @Convert(converter = NrcTypeConverter.class)
    private NrcType type;

    @ManyToOne
    @JoinColumn(name = "nrc_code_id", nullable = false)
    private NrcCode nrcCode;

    @OneToOne
    @JoinColumn(name = "yogi_id", nullable = false)
    private Yogi yogi;

    private String formatNrc(String nrcCode, String typeName) {
        return nrcCode + "(" + typeName + ")" + postFixDigit;
    }

    public String getMmNrc() {
        return formatNrc(nrcCode.getMmNrcCode(), type.getMmName());
    }

    public String getEnNrc() {
        return formatNrc(nrcCode.getEnNrcCode(), type.getEnName());
    }
}
