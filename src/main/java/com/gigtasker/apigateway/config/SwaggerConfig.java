package com.gigtasker.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SwaggerConfig {

    @Bean
    public RouterFunction<ServerResponse> swaggerRouter() {
        // Redirects the root swagger page to the webjar resource with the config URL
        return route(GET("/swagger-ui.html"),
                req -> ServerResponse.temporaryRedirect(
                        URI.create("/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config")
                ).build());
    }
}