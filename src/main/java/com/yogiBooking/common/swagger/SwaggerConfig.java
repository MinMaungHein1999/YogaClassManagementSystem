package com.yogiBooking.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Yoga Class Management System").version("1.0")
						.description("API Documentation for the Project")
						.contact(new Contact().name("Min Maung Hein").email("minmaunghein1999@email.com")))
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.components(new Components().addSecuritySchemes("Bearer Authentication",
						new SecurityScheme().name("Bearer Authentication").type(SecurityScheme.Type.HTTP)
								.scheme("bearer").bearerFormat("JWT")));
	}
}
