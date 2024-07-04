package com.t1.authjwtpattern.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/test")
public class TestPublicController {
    @GetMapping
    public String testPublic() {
        return "success";
    }
}
