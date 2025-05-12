package com.yogiBooking.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yogiBooking.common.converter.StatusConverter;
import com.yogiBooking.common.entity.constants.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User extends MasterData {

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column
    private String resetPasswordToken;
    @Column
    private String resetPasswordSentAt;
    @Column
    private LocalDateTime confirmedAt;
    @Column
    private Long otpGeneratedAt;
    @Column
    private String otp;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id", referencedColumnName = "ID", insertable = false, updatable = false, nullable = true)
    private Role role;

    @Column(name = "role_id", nullable = true)
    private Long roleId;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status;
}
