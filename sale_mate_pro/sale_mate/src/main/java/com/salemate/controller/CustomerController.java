package com.salemate.controller;

import cn.dev33.satoken.stp.StpUtil;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salemate.bo.CustomerBo;
import com.salemate.model.Customer;
import com.salemate.service.CustomerService;
import com.salemate.common.util.PageUtils;
import com.salemate.vo.CustomerSyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/getList")
    public ResponseEntity getList(@RequestBody CustomerBo bo){
        Page<Customer> list = customerService.queryPage(bo);
        return ResponseEntity.ok(new PageUtils(list));
    }
    @GetMapping("/getCustomer")
    public ResponseEntity getCustomer(String id){
        Customer customer = customerService.getById(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/syncCustomer")
    public ResponseEntity syncCustomer(){
        String corpId = StpUtil.getTokenSession().getString("corpId");
        log.info(">>>> syncCustomer:corpid:{}" ,corpId);
        CustomerSyncVo customerSyncVo = customerService.syncCustomer(corpId);
        return ResponseEntity.ok(customerSyncVo);
    }

    @GetMapping("/syncCustomerBillTag")
    public ResponseEntity syncCustomerBillTag(String startTime, String endTime){
        String corpId = StpUtil.getTokenSession().getString("corpId");
        customerService.syncCustomerBillTag(corpId,startTime,endTime);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/getSyncCustomerStatus")
    public ResponseEntity getSyncCustomerStatus(){
        String corpId = StpUtil.getTokenSession().getString("corpId");
        return ResponseEntity.ok(customerService.getSyncCustomerStatus(corpId));
    }


}
