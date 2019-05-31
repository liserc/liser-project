package com.liser.leaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author LISER
 * @date 2019/5/12
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LiserLeafWebApp {

    public static void main(String[] args) {
        SpringApplication.run(LiserLeafWebApp.class, args);
    }
}
