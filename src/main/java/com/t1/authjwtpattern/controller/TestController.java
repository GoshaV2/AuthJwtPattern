package com.t1.authjwtpattern.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@SecurityRequirement(name = "bearerAuth")
public class TestController {
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String testAdmin() {
        return "success admin";
    }

    @GetMapping("/user")
    public String testUser() {
        return "success user";
    }
}
