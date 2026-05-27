package org.example.spring.billingbatch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI billingBatchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Billing Batch API")
                        .description("정기결제 및 재시도 배치 관련 API 명세서")
                        .version("v0.0.1"));
    }
}
