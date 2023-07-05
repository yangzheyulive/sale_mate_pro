package com.salemate.service;

import java.util.List;
import com.salemate.model.Suite;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SuiteService extends IService<Suite> {


    int updateBatch(List<Suite> list);

    int batchInsert(List<Suite> list);

}


