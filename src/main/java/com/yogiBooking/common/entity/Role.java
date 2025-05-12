package com.yogiBooking.common.entity;

import com.yogiBooking.common.converter.StatusConverter;
import com.yogiBooking.common.entity.constants.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "roles")
public class Role extends MasterData{
    @Column
    private String name;
    @Column
    private String description;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status;

}
