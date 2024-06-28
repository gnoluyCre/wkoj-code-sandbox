package com.gnluy.wkojcodesandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author IGR
 * @version 1.0
 * @description: TODO
 * @date 28/6/2024 下午10:11
 */
@RestController
@RequestMapping("/")
public class HealthController {

    @GetMapping("/health")
    public String healthCheck2() {
        return "ok";
    }
}

