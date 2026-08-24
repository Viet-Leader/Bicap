package com.bicap.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Hello Admin";
    }

    @GetMapping("/api/test/farm")
    @PreAuthorize("hasRole('FARM')")
    public String farm() {
        return "Hello Farm";
    }

    @GetMapping("/api/test/retailer")
    @PreAuthorize("hasRole('RETAILER')")
    public String retailer() {
        return "Hello Retailer";
    }

}