package com.salemate.service;

import com.alibaba.fastjson.JSONObject;
import com.salemate.model.User;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {


    int updateBatch(List<User> list);

    int batchInsert(List<User> list);

    public List<JSONObject> getList();


}


