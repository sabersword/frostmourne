package org.ypq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("test")
    public String test(int age, String name) {
        return System.getProperty("java.home");
    }
}
