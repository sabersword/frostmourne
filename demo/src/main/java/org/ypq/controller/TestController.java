package org.ypq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("test")
    public String test(int age, String name) {
        // 年龄的取值范围是[0,150]
        if (age < 0) {
            throw new IllegalArgumentException("年龄错误");
        }
        return "Hello, " + name;
    }
}
