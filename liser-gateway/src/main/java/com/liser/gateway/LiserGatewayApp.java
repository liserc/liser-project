package com.liser.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author LISER
 * @date 2019/5/3
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.liser.uus.api"})
public class LiserGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(LiserGatewayApp.class, args);
    }
}
