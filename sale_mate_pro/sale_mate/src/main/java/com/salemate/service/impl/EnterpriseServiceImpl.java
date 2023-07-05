package com.salemate.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.salemate.mapper.SuiteMapper;
import com.salemate.model.User;
import com.salemate.common.util.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.model.Enterprise;
import com.salemate.mapper.EnterpriseMapper;
import com.salemate.service.EnterpriseService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise> implements EnterpriseService {

    @Resource
    private  SuiteMapper suiteMapper;



    @Override
    public int updateBatch(List<Enterprise> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<Enterprise> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    public Enterprise getEnterpriseByCorpId(String corpId) {
        List<Enterprise> list = lambdaQuery()
                .eq(Enterprise::getCorpId, corpId)
                .eq(Enterprise::getStatus,1)
                .list();
        if (CollUtil.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 安装应用
     * @param authCode
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject install(String authCode) {
        JSONObject authInfo = WxApiUtil.getPermanentCode( authCode);
        JSONObject authUserInfo = authInfo.getJSONObject("auth_user_info");
        String userid = authUserInfo.getString("userid");
        String corpId = authInfo.getJSONObject("auth_corp_info").getString("corpid");
        String permanentCode = authInfo.getString("permanent_code");
        log.info("###permanentCode:{}" , permanentCode);
        JSONObject resJson = WxApiUtil.getAuthInfo( corpId, permanentCode);
        JSONObject authCorpInfo = resJson.getJSONObject("auth_corp_info");
        Enterprise enterprise = new Enterprise();
        enterprise.setCorpId(corpId);
        enterprise.setSuiteId(WxApiUtil.getWxProperties().getSuiteId());
        enterprise.setPermanentCode(permanentCode);
        enterprise.setCorpName(authCorpInfo.getString("corp_name"));
        enterprise.setCorpType(authCorpInfo.getString("corp_type"));
        enterprise.setCorpRoundLogoUrl(authCorpInfo.getString("corp_round_logo_url"));
        enterprise.setCorpSquareLogoUrl(authCorpInfo.getString("corp_square_logo_url"));
        enterprise.setCorpUserMax(authCorpInfo.getInteger("corp_user_max"));
        enterprise.setCorpWxqrcode(authCorpInfo.getString("corp_wxqrcode"));
        enterprise.setSubjectType(authCorpInfo.getInteger("subject_type"));
        enterprise.setCorpScale(authCorpInfo.getString("corp_scale"));
        enterprise.setCorpIndustry(authCorpInfo.getString("corp_industry"));
        enterprise.setCorpSubIndustry(authCorpInfo.getString("corp_sub_industry"));
        enterprise.setStatus(1);
        saveOrUpdate(enterprise, new QueryWrapper<Enterprise>().eq("corp_id", corpId).eq("suite_id", WxApiUtil.getWxProperties().getSuiteId()));
        User user = new User();
        user.setCorpId(corpId);
        user.setUserId(userid);
        StpUtil.login(user);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        JSONObject response = new JSONObject();
        response.put("corpid", corpId);
        response.put("token", tokenInfo.getTokenValue());
        response.put("expire", tokenInfo.getTokenTimeout());
        return response;
    }
}







