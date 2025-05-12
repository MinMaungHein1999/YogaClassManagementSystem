package com.yogiBooking.common.dto;

import com.yogiBooking.common.entity.constants.Status;
import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Status status;
}
