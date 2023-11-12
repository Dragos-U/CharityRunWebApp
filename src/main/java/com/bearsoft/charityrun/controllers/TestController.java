package com.bearsoft.charityrun.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class TestController {

    @GetMapping("/test-end-point")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("You gained access to a secured endpoint");
    }
}
