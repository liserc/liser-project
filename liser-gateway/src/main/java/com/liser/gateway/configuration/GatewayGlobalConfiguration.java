package com.liser.gateway.configuration;

import com.liser.gateway.swagger.GatewaySwaggerResourcesProvider;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * @author LISER
 * @date 2019/5/9
 */
@Configuration
public class GatewayGlobalConfiguration {

    @Bean
    @Primary
    public SwaggerResourcesProvider gatewaySwaggerResourcesProvider(RouteLocator routeLocator, GatewayProperties gatewayProperties) {
        return new GatewaySwaggerResourcesProvider(routeLocator, gatewayProperties);
    }
}
