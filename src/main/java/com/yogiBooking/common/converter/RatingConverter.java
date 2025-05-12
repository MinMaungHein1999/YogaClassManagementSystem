package com.yogiBooking.common.converter;
import com.yogiBooking.common.entity.constants.Rating;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter extends BaseEnumConverter<Rating, Integer> {

    public RatingConverter() {
        super(Rating.class);
    }
}
