package com.rsm.walletflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI walletFlowOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("WalletFlow API")
                        .description("REST API for wallet management, transactions, and balance operations.")
                        .version("1.0.0"));
    }
}
