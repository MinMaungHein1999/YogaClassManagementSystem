package com.yogiBooking.common.config;


import com.yogiBooking.common.annotation.APIResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Apply the global API prefix dynamically
        configurer.addPathPrefix(apiPrefix, c -> c.isAnnotationPresent(APIResource.class));  // Applying to all controllers
    }
}
