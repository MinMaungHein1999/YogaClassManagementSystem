package com.yogiBooking.common.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class GenericMapper {

    private final ModelMapper mapper = new ModelMapper();
    /**
     * Configures the ModelMapper with custom converters and settings.
     */
    private void configureMapper() {
        this.mapper.getConfiguration().setAmbiguityIgnored(true);

        // Add custom converters
        this.mapper.addConverter(new AbstractConverter<String, Long>() {
            @Override
            protected Long convert(String source) {
                return (source != null && !source.isEmpty()) ? Long.parseLong(source) : null;
            }
        });

    }

    public <S, D> D convert(S source, Class<D> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    public <S, D> List<D> convertList(List<S> sourceList, Class<D> destinationClass) {
        return sourceList.stream()
                .map(source -> mapper.map(source, destinationClass))
                .collect(Collectors.toList());
    }

}
