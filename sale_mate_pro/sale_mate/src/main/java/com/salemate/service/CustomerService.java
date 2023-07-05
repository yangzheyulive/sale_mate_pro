package com.salemate.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salemate.bo.CustomerBo;
import com.salemate.model.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.salemate.vo.CustomerSyncVo;

public interface CustomerService extends IService<Customer> {


    int updateBatch(List<Customer> list);

    int batchInsert(List<Customer> list);

    /**
     * 同步企业标签功能
     * @param corpId
     * @return
     */
    CustomerSyncVo syncCustomer(String corpId);



    void syncCustomerBillTag(String corpId,String startTime,String endTime);

    CustomerSyncVo getSyncCustomerStatus(String corpId);

    Page<Customer> queryPage(CustomerBo bo);
}


