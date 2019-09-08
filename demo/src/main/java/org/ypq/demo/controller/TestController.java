package org.ypq.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ypq.demo.pojo.Company;

import java.util.Random;

@RestController
public class TestController {

    @GetMapping("register")
    public String test(int age, String name, Company company) {

        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 年龄的取值范围是[0,150]
        if (age < 0) {
            throw new IllegalArgumentException("年龄错误");
        }
        return "Hello " + name + ", your company is " + company.getCompanyName();
    }

}
