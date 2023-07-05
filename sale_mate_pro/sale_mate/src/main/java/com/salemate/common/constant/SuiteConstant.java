package com.salemate.common.constant;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.salemate.common.util.WxApiUtil;
import com.salemate.mapper.SuiteMapper;
import com.salemate.model.Enterprise;
import com.salemate.model.Suite;
import com.salemate.service.EnterpriseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储Token和过期时间
 */
@Component
@Slf4j
public class SuiteConstant {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private SuiteMapper suiteMapper;

    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 应用的Token
     */
    public static Map<String, Object> suiteAccessTokenMap = new HashMap<>();

    /**
     * 企业的Token
     */
    public static Map<String, Object> accessTokenMap = new HashMap<>();

    /**
     * login的Token
     */
    public static Map<String, Object> loginAccessTokenMap = new HashMap<>();

    /**
     * 存储LoginAccessToken
     * @param suiteId
     * @param suiteAccessToken
     * @param expires
     */
    public void setLoginAccessToken(String suiteId, String suiteAccessToken, long expires){
        long currentTime = (System.currentTimeMillis() / 1000) + 600 - 30;
        String expiresKey = suiteId + "_save_time";
        String tokenKey = suiteId + "_token";
        loginAccessTokenMap.put(expiresKey, currentTime);
        loginAccessTokenMap.put(tokenKey, suiteAccessToken);
    }

    /**
     * 获取SuiteAccessToken
     * @param suiteId
     * @return
     */
    public String getLoginAccessToken(String suiteId){

        String expiresKey = suiteId + "_save_time";
        String tokenKey = suiteId + "_token";
        if(loginAccessTokenMap.containsKey(expiresKey)){
            long saveTime = (long)loginAccessTokenMap.get(expiresKey);
            long currentTime = System.currentTimeMillis() / 1000;
            if((saveTime - currentTime) > 0){
                return loginAccessTokenMap.get(tokenKey).toString();
            }
        }

        Suite suite = suiteMapper.selectById(suiteId);
        log.info(">>>> suite:{}", suite.toString());
        Map<String, Object> param = new HashMap<>();
        param.put("suite_id", suite.getLoginSuiteId());
        param.put("suite_secret", suite.getLoginSuiteSecret());
        param.put("suite_ticket", suite.getLoginSuiteTicket());
        ResponseEntity<JSONObject> resJson = restTemplate.postForEntity("https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token", param, JSONObject.class);

        System.out.println(">>>> resJson:" + resJson.toString());
        if(resJson.getBody().containsKey("suite_access_token")) {
            String token = resJson.getBody().get("suite_access_token").toString();
            setLoginAccessToken(
                    suiteId,
                    token,
                    resJson.getBody().getInteger("expires_in")

            );
            return token;
        }
        return "";
    }


    /**
     * 存储SuiteAccessToken
     * @param suiteId
     * @param suiteAccessToken
     * @param expires
     */
    public void setSuiteAccessToken(String suiteId, String suiteAccessToken, long expires){
        long currentTime = (System.currentTimeMillis() / 1000) + 600 - 30;
        String expiresKey = suiteId + "_save_time";
        String tokenKey = suiteId + "_token";
        suiteAccessTokenMap.put(expiresKey, currentTime);
        suiteAccessTokenMap.put(tokenKey, suiteAccessToken);
    }

    /**
     * 获取SuiteAccessToken
     * @param suiteId
     * @return
     */
    public String getSuiteAccessToken(String suiteId){

            String expiresKey = suiteId + "_save_time";
            String tokenKey = suiteId + "_token";
            if(suiteAccessTokenMap.containsKey(expiresKey)){
                long saveTime = (long)suiteAccessTokenMap.get(expiresKey);
                long currentTime = System.currentTimeMillis() / 1000;
                if((saveTime - currentTime) > 0){
                    return suiteAccessTokenMap.get(tokenKey).toString();
                }
            }

            Suite suite = suiteMapper.selectById(suiteId);

            Map<String, Object> param = new HashMap<>();
            param.put("suite_id", suite.getSuiteId());
            param.put("suite_secret", suite.getSuiteSecret());
            param.put("suite_ticket", suite.getSuiteTicket());
            ResponseEntity<JSONObject> resJson = restTemplate.postForEntity("https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token", param, JSONObject.class);
            System.out.println(">>>> resJson:" + resJson.toString());
            String token = resJson.getBody().get("suite_access_token").toString();
            setSuiteAccessToken(
                    suiteId,
                    token,
                    resJson.getBody().getInteger("expires_in")

            );
            return token;

    }

    /**
     * 存储AccessToken
     * @param corpId
     * @param accessToken
     * @param expires
     */
    public void setAccessToken(String corpId, String accessToken, long expires){
        long currentTime = (System.currentTimeMillis() / 1000) + (10 * 60) - 30;
        String expiresKey = corpId + "_save_time";
        String tokenKey = corpId + "_token";
        accessTokenMap.put(expiresKey, currentTime);
        accessTokenMap.put(tokenKey, accessToken);
    }

    /**
     * 获取企业AccessToken
     * @param corpId
     * @return
     */
    public String getAccessToken(String corpId){
            String expiresKey = corpId + "_save_time";
            String tokenKey = corpId + "_token";
            if(accessTokenMap.containsKey(expiresKey)){
                long currentTime = System.currentTimeMillis() / 1000;
                long saveTime = (long)accessTokenMap.get(expiresKey);
                if((saveTime - currentTime) > 0){
                    return accessTokenMap.get(tokenKey).toString();
                }
            }
            Enterprise enterprise = enterpriseService.getOne(new QueryWrapper<Enterprise>()
                    .eq("corp_id", corpId)
                    .eq("suite_id", WxApiUtil.getWxProperties().getSuiteId()));

            System.out.println(">>>> ");
            String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=" + getSuiteAccessToken(enterprise.getSuiteId());
            Map<String, Object> param = new HashMap<>();
            param.put("auth_corpid", enterprise.getCorpId());
            param.put("permanent_code", enterprise.getPermanentCode());
            ResponseEntity<JSONObject> resJson = restTemplate.postForEntity(url, param, JSONObject.class);
            System.out.println(resJson);
            String token = resJson.getBody().get("access_token").toString();
            setAccessToken(
                    corpId,
                    token,
                    resJson.getBody().getInteger("expires_in")

            );
            return token;
    }

}
