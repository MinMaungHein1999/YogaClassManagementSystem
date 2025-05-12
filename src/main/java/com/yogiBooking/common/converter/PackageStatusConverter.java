package com.yogiBooking.common.converter;
import com.yogiBooking.common.entity.constants.PackageStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PackageStatusConverter extends BaseEnumConverter<PackageStatus, Integer> {
    public PackageStatusConverter() {
        super(PackageStatus.class);
    }
}

