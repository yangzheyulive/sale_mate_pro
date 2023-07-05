package com.salemate.controller;

import com.alibaba.fastjson.JSONObject;
import com.salemate.service.EnterpriseService;
import com.salemate.service.SuiteService;
import com.salemate.vo.WxAuthVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wxApi")
public class WxApiController {

    @Resource
    private RestTemplate restTemplate;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private SuiteService suiteService;

    @PostMapping("/auth")
    public ResponseEntity getAuth(WxAuthVO auth){
        //restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo", );?access_token=YsomPP-qb1XA21AtFE0-7Vp1Oe64mCm2JF4iEu5LETVJ9Jk8TRTSfmJbGi2vwiuTdvprIi86um4iAeYhS_XQYrRL4ELWJn7HqhK8uiz6wkzXEg1LfJsGv4Phucn7ae1Uh9udWbfX40KC7EgPAxE6KgYx2dTCE0gA3uYRZcagjFbWDkUgCDCdSbI2xOdndCYt0senT8nw0W9rNiawdjpN8A&code=dFHTRVyI0JIzLGm6LvV4uGcrEc7c17A6EW0CI-RHg9E)
        return ResponseEntity.ok(getAccessToken());
    }

    private String getAccessToken(){
        Map param = new HashMap();
        param.put("corpid", "ww2ed1275aedf3d2ce");
        param.put("corpsecret", "xxApuBnZG67XCeoRy_5bzCATtD8vKrm-jZv-rWFfvdI");
        ResponseEntity result = restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/gettoken", String.class, param);
        System.out.println(result.toString());
        if(result.getStatusCode() == HttpStatus.OK && result.hasBody()){
            System.out.println("res:"+result.getBody().toString());
            JSONObject resultJson = JSONObject.parseObject(result.getBody().toString());
            return resultJson.get("access_token").toString();
        }
        return "";
    }






}
