package com.salemate.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.salemate.model.Enterprise;
import com.baomidou.mybatisplus.extension.service.IService;

public interface EnterpriseService extends IService<Enterprise> {


    int updateBatch(List<Enterprise> list);

    int batchInsert(List<Enterprise> list);


    Enterprise getEnterpriseByCorpId(String corpId);

    JSONObject install(String authCodeJson);
}







