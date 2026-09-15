package org.shaloman.pj.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 설정.
 *
 * 접속 URL:
 *   - Swagger UI: http://localhost:8080/swagger-ui.html
 *   - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Faith Pathway (Holylife) API")
                        .description("말씀과 독서로 자라나는 신앙의 길 — RESTful API 문서")
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("ban-jh")
                                .email("ban-jh@users.noreply.github.com")
                                .url("https://github.com/ban-jh/holylife"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}