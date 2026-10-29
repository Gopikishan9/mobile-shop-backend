package com.gopikishan.mobileshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health Check API")
public class HealthCheckApi {

    @GetMapping("/")
    public String mobileShop(){
        System.out.println("Mobile Shop");
        return "Mobile Shop";
    }

    @GetMapping("/health")
    public String healthCheck(){
        System.out.println("Mobile Shop");
        return "Health Check";
    }

    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Testing internal error");
    }
}
