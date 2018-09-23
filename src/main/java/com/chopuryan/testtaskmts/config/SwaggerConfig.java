package com.chopuryan.testtaskmts.config;

import com.chopuryan.testtaskmts.rest.TaskController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@PropertySource("classpath:swagger.properties")
@ComponentScan(basePackageClasses = TaskController.class)
@Configuration
public class SwaggerConfig {
    private static final String SWAGGER_API_VERSION = "1";
    private static final String LICENSE_TEXT = "License";
    private static final String TITLE = "Task REST API";
    private static final String DESCRIPTION = "RESTful API for Task";
    private static final String PATH_MAPPING = "/";
    private static final String PATH_REGEX = "/api.*";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(TITLE)
                .description(DESCRIPTION)
                .license(LICENSE_TEXT)
                .version(SWAGGER_API_VERSION)
                .build();
    }

    @Bean
    public Docket taskApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping(PATH_MAPPING)
                .select()
                .paths(PathSelectors.regex(PATH_REGEX))
                .build();
    }
}
