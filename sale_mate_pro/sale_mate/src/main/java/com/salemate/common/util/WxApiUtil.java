package com.salemate.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.salemate.common.config.WxProperties;
import com.salemate.model.Suite;
import com.salemate.service.SuiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WxApiUtil {

//    private final static TimedCache<String, String> cacheMap = CacheUtil.newTimedCache(9999999);

    private static final RedisUtils redisUtils = SpringUtil.getBean(RedisUtils.class);
    private static final RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);

    private static final WxProperties CORP_WX_CONFIG = SpringUtil.getBean("corpWxConfig");

    private static final SuiteService suiteService = SpringUtil.getBean(SuiteService.class);
    //获取第三方应用凭证
    private static final String GET_SUITE_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";
    //获取预应用二维码
    private static final String GET_APP_QRCODE = "https://qyapi.weixin.qq.com/cgi-bin/service/get_app_qrcode?suite_access_token=";
    //获取应用预授权码
    private static final String GET_PRE_AUTH_CODE = "https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=";
    //获取企业永久授权码
    private static final String GET_PERMANENT_CODE = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=";
    //获取企业授权信息
    private static final String GET_AUTH_INFO = "https://qyapi.weixin.qq.com/cgi-bin/service/get_auth_info?suite_access_token=";
    //应用安装授权链接
    private static final String INSTALL_URL = "https://open.work.weixin.qq.com/3rdapp/install";
    //openUserId转换成userId
    private static final String CONVERT_TO_USERID = " https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_userid?access_token=";
    //获取客户id列表
    private static final String EXTERNALCONTACT_LIST = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/list";
    //获取客户详情列表
    private static final String EXTERNALCONTACT_BATCH = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/batch/get_by_user?access_token=";
    private static final String GETTOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    //获取企业access_token
    private static final String GET_CORP_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=";
    //读取部门成员id列表
    private static final  String USER_ID_LIST_URL =  "https://qyapi.weixin.qq.com/cgi-bin/user/list_id?access_token=";
    //读取成员信息
    private static final String USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
    //上传临时素材
    private static final String UPLOAD_TEMP_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/upload";
    //获取企业标签
    private static final String GET_CORP_TAG_LIST = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get_corp_tag_list?access_token=";
    private static final String ADD_CORP_TAG =  "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token=";
    //创建群聊
    private static final String ADD_MSG_TEMPLATE = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_msg_template?access_token=";
    //创建朋友圈
    private static final String ADD_MOMENT_TASK = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_moment_task?access_token=";
    //获取对外收款记录
    private static final String GET_BILL_LIST =  "https://qyapi.weixin.qq.com/cgi-bin/externalpay/get_bill_list?access_token=";
    private static final String MARK_TAG = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/mark_tag?access_token=";
    //获取客户详情
    private static final String GET_EXTERNALCONTACT = "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get";
    //上传临时附件
    public static final  String UPLOAD_ATTACHMENT = "https://qyapi.weixin.qq.com/cgi-bin/media/upload_attachment";
    /**
     * 获取第三方应用凭证SuiteAccessToken
     *
     * @param count 重试次数
     */
    public static void setSuiteAccessToken(Integer count) {
        if (count == null) {
            count = 3;
        }
        if (count <= 0) {
            //TODO 报警以邮件形式通知管理员
            throw new RuntimeException("获取第三方应用凭证失败");
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("suite_id", CORP_WX_CONFIG.getSuiteId());
        params.put("suite_secret", CORP_WX_CONFIG.getSuiteSecret());
        params.put("suite_ticket", getSuiteTicket());
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_SUITE_TOKEN_URL, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String suite_access_token = body.getString("suite_access_token");
        if (StrUtil.isBlank(suite_access_token)) {
            //40085 suite_ticket过期
            if (body.getInteger("errcode") == 40085) {
                setSuiteTicket();
                setSuiteAccessToken(count - 1);
            } else {
                throw new RuntimeException("获取第三方应用凭证失败");
            }
        }
        redisUtils.set(WxConstantKeys.SUITE_ACCESS_TOKEN, suite_access_token, 7200);
    }


    /**
     * 获取第三方应用凭证SuiteAccessToken
     *
     * @return
     */
    public static String getSuiteAccessToken() {
        String suite_access_token = redisUtils.get(WxConstantKeys.SUITE_ACCESS_TOKEN);
        if (StrUtil.isBlank(suite_access_token)) {
            setSuiteAccessToken(5); //如果没有就重新获取
            suite_access_token = redisUtils.get(WxConstantKeys.SUITE_ACCESS_TOKEN);
        }
        return suite_access_token;
    }

    /**
     * 更新SuiteTicket
     *
     * @return
     */
    public static void setSuiteTicket() {
        Suite suite = suiteService.getById(CORP_WX_CONFIG.getSuiteId());
        //十分钟过期
        redisUtils.set(WxConstantKeys.SUITE_TICKET, suite.getSuiteTicket(), 3600);
    }

    /**
     * 获取SuiteTicket
     *
     * @return
     */
    public static String getSuiteTicket() {
        String suite_ticket = redisUtils.get(WxConstantKeys.SUITE_TICKET);
        if (StrUtil.isBlank(suite_ticket)) {
            setSuiteTicket();
            suite_ticket = redisUtils.get(WxConstantKeys.SUITE_TICKET);
        }
        return suite_ticket;
    }

    /**
     * 获取应用二维码
     */
    public static String getAppQrcode() {
        Map<String, Object> params = new HashMap<>();
//        params.put("suite_access_token", suite_access_token);
        params.put("result_type", 2);
        params.put("suite_id", CORP_WX_CONFIG.getSuiteId());
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_APP_QRCODE + getSuiteAccessToken(), params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String qrcode = body.getString("qrcode");
        if (StrUtil.isBlank(qrcode)) {
            throw new RuntimeException("获取应用二维码失败");
        }
        return qrcode;
    }


    /**
     * 获取预授权码
     */
    public static String getPreAuthCode() {
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_PRE_AUTH_CODE + getSuiteAccessToken(), new HashMap<>(), JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String pre_auth_code = body.getString("pre_auth_code");
        if (StrUtil.isBlank(pre_auth_code)) {
            throw new RuntimeException("获取预授权码失败");
        }
        return pre_auth_code;
    }

    /**
     * 构建二维码安装链接
     */
    public static String buildInstallUrl(String pre_auth_code) {
        List<String> trustHostList = CORP_WX_CONFIG.getTrustHostList();
        //随机10个字母A-Z或者数字0-9
        String state = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER, 10);
        String url = INSTALL_URL+"?suite_id=" + CORP_WX_CONFIG.getSuiteId() + "&pre_auth_code=" + pre_auth_code + "&redirect_uri=" + trustHostList.get(0) + "&state=" + state;
        return url;
    }

    public static WxProperties getWxProperties() {
        return CORP_WX_CONFIG;
    }

    /**
     * 获取永久授权码
     *
     * @param auth_code 授权码
     */
    public static JSONObject getPermanentCode(String auth_code) {
        Map<String, Object> params = new HashMap<>();
        params.put("auth_code", auth_code);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_PERMANENT_CODE + getSuiteAccessToken(), params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        log.info("获取永久授权码返回结果：{}", body);
        if (!body.containsKey("permanent_code")) {
            throw new RuntimeException("获取永久授权码");
        }
        return body;
    }


    /**
     * 获取企业授权信息
     */
    public static JSONObject getAuthInfo(String auth_corpid, String permanent_code) {
        Map<String, Object> params = new HashMap<>();
        params.put("auth_corpid", auth_corpid);
        params.put("permanent_code", permanent_code);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_AUTH_INFO + getSuiteAccessToken(), params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        log.info("获取企业授权信息返回结果：{}", body);
        if (!body.containsKey("auth_corp_info")) {
            throw new RuntimeException("获取企业授权信息失败");
        }
        return body;
    }

    /**
     * openUserId转换成userId
     */
    public static String convertToUserId(String openUserId) {
        Map<String, Object> params = new HashMap<>();
        params.put("openid", openUserId);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(CONVERT_TO_USERID + getSuiteAccessToken(), params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("openUserId转换成userId失败");
        }
        return body.getString("userid");
    }

    /**
     * 获取成员下客户id列表
     */
    public static JSONObject getExternalContactIdList(String userId) {

        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(EXTERNALCONTACT_LIST + "?access_token=" + getSuiteAccessToken() + "&userid" + userId, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        log.info("获取成员下客户列表返回结果：{}", body);
        if (!body.containsKey("external_userid")) {
            throw new RuntimeException("获取成员下客户列表失败");
        }
        return body;
    }

    /**
     * 获取客户详情列表
     */
    public static JSONObject getExternalContactDetail(List<String> external_userid,String accessToken,String cursor) {
        Map<String, Object> params = new HashMap<>();
        params.put("userid_list", external_userid);
        params.put("cursor", cursor);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(EXTERNALCONTACT_BATCH + accessToken, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        log.info("获取客户详情列表返回结果：{}", body);
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("获取客户详情列表失败");
        }
        return body;
    }


    /**
     * 获取应用功能权限
     */
    private static String getAppAccessToken(String corpid, String corpsecret) {
        ResponseEntity<JSONObject> forEntity = restTemplate.getForEntity(GETTOKEN + "?corpid=" + corpid + "&corpsecret=" + corpsecret, JSONObject.class);
        JSONObject body = forEntity.getBody();
        return body.getString("access_token");
    }

    /**
     * 获取客户接口权限
     *
     * @param corpid
     * @param corpsecret 客户secret
     */
    public static String getCustomerAccessToken(String corpid, String corpsecret) {
        String customerToken = redisUtils.get(WxConstantKeys.CUSTOMER_ACCESS_TOKEN + corpid);
        if (StrUtil.isBlank(customerToken)) {
            customerToken = getAppAccessToken(corpid, corpsecret);
            redisUtils.set(WxConstantKeys.CUSTOMER_ACCESS_TOKEN + corpid, customerToken, 7200);
        }
        return customerToken;
    }

    /**
     * 获取通讯录接口权限
     *
     * @param corpid
     * @param corpsecret 通讯录secret
     */
    public static String getUserAccessToken(String corpid, String userSecret) {
        String userToken = redisUtils.get(WxConstantKeys.USER_ACCESS_TOKEN + corpid);
        if (StrUtil.isBlank(userToken)) {
            userToken = getAppAccessToken(corpid, userSecret);
            redisUtils.set(WxConstantKeys.USER_ACCESS_TOKEN + corpid, userToken, 7200);
        }
        return userToken;
    }
    /**
     * 获取通讯录接口权限
     *
     * @param corpid
     * @param corpsecret 通讯录secret
     */
    public static String getCorpPayAccessToken(String corpid, String paySecret) {
        String userToken = redisUtils.get(WxConstantKeys.CORP_PAY_TOKEN + corpid);
        if (StrUtil.isBlank(userToken)) {
            userToken = getAppAccessToken(corpid, paySecret);
            redisUtils.set(WxConstantKeys.CORP_PAY_TOKEN + corpid, userToken, 7200);
        }
        return userToken;
    }

    /**
     * 设置企业access_token
     */
    public static void setCorpToken(String corpid, String permanent_code) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("auth_corpid", corpid);
        params.put("permanent_code", permanent_code);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_CORP_TOKEN + getSuiteAccessToken(), params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        if (!body.containsKey("access_token")) {
            throw new RuntimeException("获取企业access_token失败");
        }
        redisUtils.set(WxConstantKeys.CORP_ACCESS_TOKEN + corpid, body.getString("access_token"), 7200);
    }

    /**
     * 获取企业access_token
     */
    public static String getCorpToken(String corpid, String permanent_code) {
        String corpToken = redisUtils.get(WxConstantKeys.CORP_ACCESS_TOKEN + corpid);
        if (StrUtil.isBlank(corpToken)) {
            setCorpToken(corpid, permanent_code);
            corpToken = redisUtils.get(WxConstantKeys.CORP_ACCESS_TOKEN + corpid);
        }
        return corpToken;
    }

    /**
     * 获取通许录列表
     * @param accessToken
     * @return
     */
    public static JSONArray getUserIds(String accessToken) {
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(USER_ID_LIST_URL + accessToken, null, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("获取通许录列表失败");
        }
        return body.getJSONArray("dept_user");
    }
    /**
     * 获取通许录成员详细信息
     */
    public static JSONObject getUserInfo(String accessToken, String userId) {
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(USER_INFO_URL+"?access_token="+accessToken+"&userid="+userId, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("获取通许录成员详细信息失败");
        }
        return body;
    }

    /**
     * 上传临时素材
     * @param uploadFile
     * @return
     */
    public static JSONObject uploadTemp(byte[] bytes,String fileName, String corpid, String permanent_code) throws IOException {
        HttpResponse media = HttpUtil.createPost(UPLOAD_TEMP_URL + "?access_token=" + getCorpToken(corpid, permanent_code) + "&type=image")
                .form("media",bytes,fileName)
                .contentType("multipart/form-data")
                .execute();
        JSONObject res = JSONObject.parseObject(media.body());
        String errcode = res.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("上传临时素材失败");
        }
        return res;
    }

    /**
     * 获取企业标签
     */
    public static JSONObject getTagCorpList(String access_token,JSONArray tag_id){
        HashMap<String, Object> params = new HashMap<>();
        params.put("tag_id", tag_id);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_CORP_TAG_LIST + access_token, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("获取企业标签失败");
        }
        return body;
    }
    /**
     * 创建群聊
     */
    public static JSONObject addMsgTemplate(String access_token,List<String> external_userid,String content,List<String> media_id){
        HashMap<String, Object> params = new HashMap<>();
        params.put("external_userid", external_userid); //要发送的客户
        //文本消息
        JSONObject text = new JSONObject();
        text.put("content",content);
        params.put("text",text);
        //图片消息
        JSONArray attachments = new JSONArray();
        for (int i = 0; i < media_id.size(); i++) {
            JSONObject attachmentsItem = new JSONObject();
            attachmentsItem.put("msgtype","image");
            JSONObject image = new JSONObject();
            image.put("media_id",media_id.get(i));
            attachmentsItem.put("image",image);
            attachments.add(attachmentsItem);
        }
        params.put("attachments",attachments);
        //图片ID
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(ADD_MSG_TEMPLATE + access_token, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("创建群聊失败");
        }
        return body;
    }

    /**
     * 获取对外收款记录
     * @param access_token
     * @param begin_time
     * @param end_time
     * @return
     */
    public static JSONObject getBillList(String access_token,Long begin_time,Long end_time,String cursor){
        HashMap<String, Object> params = new HashMap<>();
        params.put("begin_time", begin_time); //要发送的客户
        params.put("end_time", end_time); //要发送的客户
        params.put("cursor", cursor); //要发送的客户
        log.info(">>>>>>对外收款记录access_token:{}",access_token);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(GET_BILL_LIST + access_token, params, JSONObject.class);

        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("获取对外收款记录失败");
        }
        return body;

    }

    public static JSONObject addCorpTag(String customerAccessToken, JSONObject tagGroup, List<JSONObject> tagList) {
        tagGroup.put("tag",tagList);
        //创建标签
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(ADD_CORP_TAG + customerAccessToken, tagGroup, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("创建标签失败");
        }
        return body;
    }

    public static JSONObject markTag(String externalUserid,String userId,List<String> add_tag,List<String> remove_tag,String customerAccessToken) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userid", userId); //要发送的客户
        params.put("add_tag", add_tag); //要发送的客户
        params.put("remove_tag", remove_tag); //要发送的客户
        params.put("external_userid", externalUserid); //要发送的客户
        //创建标签
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(MARK_TAG + customerAccessToken, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("标记客户失败");
        }
        return body;
    }

    public static JSONObject getCustomerInfo(String customerAccessToken, String external_userid) {
        ResponseEntity<JSONObject> forEntity = restTemplate.getForEntity(GET_EXTERNALCONTACT + "?access_token=" + customerAccessToken + "&external_userid=" + external_userid, JSONObject.class);
        JSONObject body = forEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("获取客户信息失败");
        }
        return body;
    }

    public static JSONObject addMomentTask(String access_token,List<String> tag_list,String content,List<String> media_id){
        HashMap<String, Object> params = new HashMap<>();
        JSONObject visible_range = new JSONObject();
        JSONObject external_contact_list = new JSONObject();
        external_contact_list.put("tag_list",tag_list);
        visible_range.put("external_contact_list",external_contact_list);

        params.put("visible_range", visible_range); //要发送的客户
        //文本消息
        JSONObject text = new JSONObject();
        text.put("content",content);
        params.put("text",text);
        //图片消息
        JSONArray attachments = new JSONArray();
        for (int i = 0; i < media_id.size(); i++) {
            JSONObject attachmentsItem = new JSONObject();
            attachmentsItem.put("msgtype","image");
            JSONObject image = new JSONObject();
            image.put("media_id",media_id.get(i));
            attachmentsItem.put("image",image);
            attachments.add(attachmentsItem);
        }
        params.put("attachments",attachments);
        //图片ID
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(ADD_MOMENT_TASK + access_token, JSONUtil.parse(params), JSONObject.class);
        JSONObject body = responseEntity.getBody();
        String errcode = body.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("创建群聊失败");
        }
        return body;
    }

    /**
     * 上传附件
     */
    public static JSONObject uploadAttachment(byte[] bytes,String fileName, String access_token) {
//        Content-Disposition: form-data; name="media";filename="wework.txt"; filelength=6
//        POST的请求包中，form-data中媒体文件标识，应包含有 filename、filelength、content-type等信息
        HttpResponse media = HttpUtil.createPost(UPLOAD_ATTACHMENT + "?access_token=" + access_token+ "&media_type=image&attachment_type=1")
                .form("media", bytes, fileName)
                .contentType("multipart/form-data")
                .contentType("application/octet-stream")
                .header("Content-Disposition", "form-data; name=\"media\";filename=\"" + fileName + "\"; filelength=" + bytes.length)
                .execute();
        String body = media.body();
        JSONObject jsonObject = JSON.parseObject(body);
        String errcode = jsonObject.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new RuntimeException("上传附件失败");
        }
        return jsonObject;
    }

}











