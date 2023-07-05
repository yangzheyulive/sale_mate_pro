package com.salemate.controller;

import com.salemate.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/enterprise")
@RestController
public class EnterpriseController {
    @Autowired
    private EnterpriseService enterpriseService;

}
