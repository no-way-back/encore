package com.nowayback.funding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FundingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundingApplication.class, args);
    }

}
