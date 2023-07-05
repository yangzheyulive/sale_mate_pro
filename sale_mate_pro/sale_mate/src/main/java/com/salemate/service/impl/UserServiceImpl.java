package com.salemate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.salemate.mapper.EnterpriseMapper;
import com.salemate.model.Enterprise;
import com.salemate.common.util.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.UserMapper;
import com.salemate.model.User;
import com.salemate.service.UserService;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private EnterpriseMapper enterpriseMapper;


    @Override
    public int updateBatch(List<User> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<User> list) {
        return baseMapper.batchInsert(list);
    }

    public List<JSONObject> getList(){
        String corpId = StpUtil.getTokenSession().getString("corpId");
        log.info(">>> corpId:{}",corpId);
        Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().eq("corp_id",corpId).eq("suite_id",WxApiUtil.getWxProperties().getSuiteId()));
        String permanentCode = enterprise.getPermanentCode();
        String userSecret = enterprise.getUserSecret();
        //获取企业的token
        String corpToken = WxApiUtil.getCorpToken(corpId, permanentCode);
        //获取企业列表
        String userAccessToken = WxApiUtil.getUserAccessToken(corpId, userSecret);

        JSONArray userIds = WxApiUtil.getUserIds(userAccessToken);
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            JSONObject userInfoJson = userIds.getJSONObject(i);
            String userid = userInfoJson.getString("userid");
            JSONObject userInfo = null;
            try {
                 userInfo = WxApiUtil.getUserInfo(corpToken, userid);
            }catch (Exception e){
                log.error(">>> 获取用户信息失败:{}",e.getMessage());
                continue;
            }
            list.add(userInfo);
        }
        String customerAccessToken = WxApiUtil.getCustomerAccessToken(corpId, enterprise.getCustomerSecret());
        List<String> userIdList = CollUtil.getFieldValues(userIds, "userid", String.class);
        JSONObject externalContactDetail = WxApiUtil.getExternalContactDetail(userIdList, customerAccessToken,null);
        log.info(">>> externalContactDetail:{}",externalContactDetail);
        return userIds.toJavaList(JSONObject.class);
    }
}


