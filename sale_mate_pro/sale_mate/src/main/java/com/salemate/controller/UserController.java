package com.salemate.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.salemate.bo.UserInstallBo;
import com.salemate.common.config.WxProperties;
import com.salemate.common.constant.SuiteConstant;
import com.salemate.model.Enterprise;
import com.salemate.model.User;
import com.salemate.service.EnterpriseService;
import com.salemate.service.UserService;
import com.salemate.common.util.WxApiUtil;
import com.salemate.vo.WxAuthVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private SuiteConstant suiteConstant;

    @Autowired
    private EnterpriseService enterpriseService;

    @Value("${spring.profiles.include}")
    private String env;

    private final WxProperties wxProperties = WxApiUtil.getWxProperties();

    private final static String  LOGIN_SIGNER = "17A93D2F5F487E8A8C734F8E0D47E6D9";


    @PostMapping("/verify")
    public ResponseEntity verify(@RequestBody WxAuthVO wxAuth){
        log.info("wxAuth:" + wxAuth.toString());
        String state = wxAuth.getState();
        // TODO 通过企业微信state判断  weblogin 网页登录   workpc 工作台登录
        String accessToken = "";
        if(state.contains("weblogin_")){
            accessToken = suiteConstant.getLoginAccessToken(state.replace("weblogin_", ""));
        } else {
            accessToken = suiteConstant.getSuiteAccessToken(state);
        }
        if(StringUtils.hasText(wxAuth.getCode())){
            String url = "https://qyapi.weixin.qq.com/cgi-bin/service/auth/getuserinfo3rd?suite_access_token=" + accessToken + "&code=" + wxAuth.getCode();
            ResponseEntity<JSONObject> resObj = restTemplate.getForEntity(url, JSONObject.class);
            log.info("#####resObj:" + resObj.toString());
            if(resObj.getStatusCode() == HttpStatus.OK && resObj.getBody().containsKey("corpid")){
                String userid = resObj.getBody().getString("userid");
                String corpid = resObj.getBody().getString("corpid");
                Enterprise enterprise = enterpriseService.getEnterpriseByCorpId(corpid);
                if (enterprise == null){
                    //获取预授权码
                    String preAuthCode = WxApiUtil.getPreAuthCode();
                    //构建安装二维码
                    String installUrl = WxApiUtil.buildInstallUrl(preAuthCode);
                    return ResponseEntity.ok(installUrl);
                }
                //下发token
                StpUtil.login(userid);
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                StpUtil.getTokenSession().set("corpId", corpid);

                log.debug("#####tokenInfo:" + tokenInfo.toString());
                JSONObject response = new JSONObject();
                response.put("token", tokenInfo.getTokenValue());
                response.put("expire", tokenInfo.getTokenTimeout());
                response.put("cropId", corpid);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.ok("fail");
    }

    @PostMapping("/login")
    public ResponseEntity login(WxAuthVO wxAuth){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("last_login_code", wxAuth.getCode());
        User user = userService.getOne(queryWrapper);
        if(user != null){
            //记录登录状态返回token
            return ResponseEntity.ok("");
        }
        return ResponseEntity.ok("fail");
    }


    @PostMapping("/install")
    public ResponseEntity install(@RequestBody UserInstallBo bo){
        //用户是否安装应用
        if (StrUtil.isBlank(bo.getAuthCode())){
            throw new RuntimeException("authCode is null");
        }
        JSONObject token = enterpriseService.install(bo.getAuthCode());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/list")
    public ResponseEntity list(){
        return ResponseEntity.ok(userService.getList());
    }

    @GetMapping("/env")
    public ResponseEntity env(){
        if (!"local".equals(env)){
            throw new RuntimeException("env is not local");
        }
        StpUtil.login("SongJiaYu");
        StpUtil.getTokenSession().set("corpId", "ww2ed1275aedf3d2ce");
        JSONObject response = new JSONObject();
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        response.put("token", tokenInfo.getTokenValue());
        response.put("expire", tokenInfo.getTokenTimeout());
        return ResponseEntity.ok(response);
    }


}
