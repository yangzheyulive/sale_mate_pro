package com.salemate.controller;

import com.salemate.service.SuiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private SuiteService suiteService;

    @GetMapping("/test")
    public ResponseEntity getUser(){

        return ResponseEntity.ok(suiteService.list());
    }
}
