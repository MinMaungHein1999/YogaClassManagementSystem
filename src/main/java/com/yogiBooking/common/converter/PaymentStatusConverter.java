package com.yogiBooking.common.converter;
import com.yogiBooking.common.entity.constants.PaymentStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter extends BaseEnumConverter<PaymentStatus, Integer>{
    public PaymentStatusConverter() {
        super(PaymentStatus.class);
    }
}

