package com.domaszekkk.medicalclinic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medicalClinicOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Clinic API")
                        .description("REST API for managing patients, doctors, facilities and visits in a medical clinic")
                        .version("1.0"));
    }
}