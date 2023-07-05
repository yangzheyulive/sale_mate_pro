package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.CustomerTagMapper;
import com.salemate.model.CustomerTag;
import com.salemate.service.CustomerTagService;
import org.springframework.stereotype.Service;

@Service
public class CustomerTagServiceImpl extends ServiceImpl<CustomerTagMapper, CustomerTag> implements CustomerTagService {
}
