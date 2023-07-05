package com.salemate.common.task;

import cn.hutool.core.date.DateUtil;
import com.salemate.model.Enterprise;
import com.salemate.service.CustomerService;
import com.salemate.service.EnterpriseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component( "customerSycTask" )
@Slf4j
public class CustomerSycTask implements ITask{
    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private CustomerService customerService;

    @Override
    public void run(String params) {
        List<Enterprise> enterpriseList = enterpriseService.list();
        for (Enterprise enterprise:enterpriseList) {
            if(StringUtils.hasText(enterprise.getUserSecret())){
                customerService.syncCustomer(enterprise.getCorpId());
                Date date = new Date();
                int year = DateUtil.year(date);
                int month = DateUtil.month(date) + 1;
                int lastDay = DateUtil.getLastDayOfMonth(date);
                String start = year + "-" + month + "-01";
                String end = year + "-" + month + "-" + lastDay;
                customerService.syncCustomerBillTag(enterprise.getCorpId(),start,end);
            } else {
                log.info("请给企业corpid:{},设置通讯录的Secret");
            }

        }
    }
}
