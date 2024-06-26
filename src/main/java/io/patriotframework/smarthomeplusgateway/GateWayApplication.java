package io.patriotframework.smarthomeplusgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GateWayApplication {

    /**
     * Main method, where the Spring Boot container is executed.
     *
     * @param args args given to Spring Boot application
     */
    public static void main(String[] args) throws RuntimeException {
        SpringApplication.run(GateWayApplication.class, args);
    }

}