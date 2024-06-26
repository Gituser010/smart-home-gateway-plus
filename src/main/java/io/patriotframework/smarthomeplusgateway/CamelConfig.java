package io.patriotframework.smarthomeplusgateway;

import io.patriotframework.smarthomeplusgateway.Routes.DeviceRoutes;
import io.patriotframework.smarthomeplusgateway.Routes.HouseRoutes;
import io.patriotframework.smarthomeplusgateway.Routes.RestRoutes;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CamelConfig
 * <p>
 * This class is responsible for configuring the Camel routes for the gateway.
 */
@Configuration
public class CamelConfig {

    @Bean
    public RouteBuilder endpointRoutes() {
        return new RestRoutes();
    }

    @Bean
    public RouteBuilder houseRoutes() {
        return new HouseRoutes();
    }

    @Bean
    public RouteBuilder deviceRoutes() {
        return new DeviceRoutes();
    }
}
