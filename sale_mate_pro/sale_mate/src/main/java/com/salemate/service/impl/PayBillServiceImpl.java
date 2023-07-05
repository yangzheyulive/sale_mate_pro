package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.PayBillMapper;
import com.salemate.model.PayBill;
import com.salemate.service.PayBillService;
import org.springframework.stereotype.Service;

@Service
public class PayBillServiceImpl extends ServiceImpl<PayBillMapper, PayBill> implements PayBillService {

}
