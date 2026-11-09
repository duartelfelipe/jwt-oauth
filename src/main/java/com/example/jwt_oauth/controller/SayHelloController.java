package com.example.jwt_oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SayHelloController {

    @GetMapping("/open")
    public String sayHelloOpen() {
        return "Hello! From open resource.";
    }

    @GetMapping("/restricted")
    public String sayHelloRestricted() {
        return "Hello! From restricted resource";
    }
}
