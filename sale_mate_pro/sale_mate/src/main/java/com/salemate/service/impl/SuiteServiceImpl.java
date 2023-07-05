package com.salemate.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.SuiteMapper;
import com.salemate.model.Suite;
import com.salemate.service.SuiteService;

@Service
public class SuiteServiceImpl extends ServiceImpl<SuiteMapper, Suite> implements SuiteService {

    @Override
    public int updateBatch(List<Suite> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<Suite> list) {
        return baseMapper.batchInsert(list);
    }
}


