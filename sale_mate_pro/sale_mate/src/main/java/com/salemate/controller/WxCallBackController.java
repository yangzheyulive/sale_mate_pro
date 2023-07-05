package com.salemate.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.salemate.model.Enterprise;
import com.salemate.model.Suite;
import com.salemate.service.EnterpriseService;
import com.salemate.service.SuiteService;
import com.salemate.common.aes.AesException;
import com.salemate.common.aes.WXBizMsgCrypt;
import com.salemate.common.util.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/wx/call")
@Slf4j
public class WxCallBackController {
    @Autowired
    private SuiteService suiteService;

    @Autowired
    private EnterpriseService enterpriseService;
    @GetMapping("/setting")
    public ResponseEntity wxSetting(){
        return ResponseEntity.ok("ok");
    }

    /**
     * 第一步get请求验证模板数据
     * @param sign
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     * @throws AesException
     */
    @GetMapping(value = "/loginVerify")
    public ResponseEntity wxLogin(@RequestParam("msg_signature") String sign,
                                     @RequestParam("timestamp") String timestamp,
                                     @RequestParam("nonce") String nonce,
                                     @RequestParam("echostr") String echostr) throws AesException {

        log.info(">>> 调用了loginVerify,sign:{},timestamp:{},nonce:{},echostr:{}", sign, timestamp, nonce, echostr);

        // 代开发应用模板信息 回调配置中的Token
        String sToken = "mmnkv3fSPIPyIr5j1slxIvg";
        // 通用参数中CorpID
        String sCorpID = "ww2ed1275aedf3d2ce";
        // 代开发应用模板信息 回调配置中的EncodingAESKey
        String sEncodingAESKey = "6v0KlKwFtLtr2P4OaQmVZpzMfkpFw944wpkYOVUf39k";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sEchoStr = wxcpt.VerifyURL(sign, timestamp,
                nonce, echostr);
        log.info("verifyurl echostr: " + sEchoStr);
        // 返回解析后的明文 模板回调验证通过
        return ResponseEntity.ok(sEchoStr);
    }

    /**
     * 第一步get请求验证模板数据
     * @param sign
     * @param timestamp
     * @param nonce
     * @param xmlBody
     * @return
     * @throws AesException
     */
    @PostMapping(value = "/loginVerify")
    public ResponseEntity wxPostLogin(@RequestParam("msg_signature") String sign,
                                  @RequestParam("timestamp") String timestamp,
                                  @RequestParam("nonce") String nonce,
                                      @RequestBody String xmlBody) throws AesException {

        log.info(">>> 调用了loginVerify-wxPostLogin,sign:{},timestamp:{},nonce:{},xmlBody:{}", sign, timestamp, nonce, xmlBody);

        // 代开发应用模板信息 回调配置中的Token
        String sToken = WxApiUtil.getWxProperties().getLoginToken();
        // 通用参数中CorpID
        String sCorpID = WxApiUtil.getWxProperties().getLoginSuiteID();
        // 代开发应用模板信息 回调配置中的EncodingAESKey
        String sEncodingAESKey = WxApiUtil.getWxProperties().getLoginEncodingAESKey();

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        String sMsg = wxcpt.DecryptMsg(sign, timestamp, nonce, xmlBody);
        System.out.println("after decrypt msg: " + sMsg);

        Map xmlToMap = xmlToMap(sMsg);
        System.out.println("xmlToMap: " + xmlToMap);

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("login_suite_ticket", xmlToMap.get("SuiteTicket"));
        updateWrapper.eq("login_suite_id", xmlToMap.get("SuiteId"));
        suiteService.update(null, updateWrapper);
        // 返回解析后的明文 模板回调验证通过
        return ResponseEntity.ok("success");
    }

    /**
     * 第一步get请求验证模板数据
     * @param sign
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     * @throws AesException
     */
    @GetMapping(value = "/xingxingwanwu/template")
    public ResponseEntity wxXingXingWanWuTemplate(@RequestParam("msg_signature") String sign,
                                     @RequestParam("timestamp") String timestamp,
                                     @RequestParam("nonce") String nonce,
                                     @RequestParam("echostr") String echostr) throws AesException {

        log.info(">>> 调用了wxTemplate,wxData,sign:{},timestamp:{},nonce:{},echostr:{}", sign, timestamp, nonce, echostr);

        // 代开发应用模板信息 回调配置中的Token
        String sToken = "BieQb";
        // 通用参数中CorpID
        String sCorpID = "wpdGQHEgAA1AYBDvDENiooQDe507wVtg";
        // 代开发应用模板信息 回调配置中的EncodingAESKey
        String sEncodingAESKey = "8whY4QqVgBkZdT7U6CywYx8UGRboQIUE63WPtY3hZjS";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sEchoStr = wxcpt.VerifyURL(sign, timestamp,
                nonce, echostr);
        log.info("verifyurl echostr: " + sEchoStr);
        // 返回解析后的明文 模板回调验证通过
        return ResponseEntity.ok(sEchoStr);
    }



    /**
     * 第一步get请求验证模板数据
     * @param sign
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     * @throws AesException
     */
    @GetMapping(value = "/template")
    public ResponseEntity wxTemplate(@RequestParam("msg_signature") String sign,
                                     @RequestParam("timestamp") String timestamp,
                                     @RequestParam("nonce") String nonce,
                                     @RequestParam("echostr") String echostr) throws AesException {

        log.info(">>> 调用了wxTemplate,wxData,sign:{},timestamp:{},nonce:{},echostr:{}", sign, timestamp, nonce, echostr);

        // 代开发应用模板信息 回调配置中的Token
        String sToken = "xjOo9iPGvuJVE";
        // 通用参数中CorpID
        String sCorpID = "ww2ed1275aedf3d2ce";
        // 代开发应用模板信息 回调配置中的EncodingAESKey
        String sEncodingAESKey = "ei69jE2GjOeWjiwxoPKHbLyvNBuYy7OK9oxdW9KA4rM";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sEchoStr = wxcpt.VerifyURL(sign, timestamp,
                nonce, echostr);
        log.info("verifyurl echostr: " + sEchoStr);
        // 返回解析后的明文 模板回调验证通过
        return ResponseEntity.ok(sEchoStr);
    }

    /**
     * 第2步企业微信通过多次回调或自己在代开发应用模板信息 回调配置中手动刷新post推送新模板票据
     * @param sign
     * @param timestamp
     * @param nonce
     * @param xmlBody
     * @return
     * @throws AesException
     */
    @PostMapping("/template")
    ResponseEntity wxCallTemplate(@RequestParam("msg_signature") String sign,
                              @RequestParam("timestamp") String timestamp,
                              @RequestParam("nonce") String nonce,
                                  @RequestBody String xmlBody) throws AesException, ParserConfigurationException, IOException, SAXException {

        log.info(">>> 调用了wxCallTemplate,wxData,sign:{},timestamp:{},nonce:{},xmlBody:{}", sign, timestamp, nonce, xmlBody);

        // 代开发应用模板信息 回调配置中的Token
        String sToken = "xjOo9iPGvuJVE";
        // 代开发应用模板信息 中的模板ID
        String sCorpID = "dk08247092dc32562c";
        // 代开发应用模板信息 回调配置中的EncodingAESKey
        String sEncodingAESKey = "ei69jE2GjOeWjiwxoPKHbLyvNBuYy7OK9oxdW9KA4rM";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        String sMsg = wxcpt.DecryptMsg(sign, timestamp, nonce, xmlBody);
        System.out.println("after decrypt msg: " + sMsg);

        //  重要步骤： 获取到企业票据信息 SuiteId:dk399837d93d90e5f9    SuiteTicket:ufKRnu8ipYTh4iVfWRIEK1VCyJKD0867OeleO2odEsE-fog_6Q3HcbXhU1MCOvwh
        //after decrypt msg: <xml><SuiteId><![CDATA[dk399837d93d90e5f9]]></SuiteId><InfoType><![CDATA[suite_ticket]]></InfoType><TimeStamp>1686226233</TimeStamp><SuiteTicket><![CDATA[ufKRnu8ipYTh4iVfWRIEK1VCyJKD0867OeleO2odEsE-fog_6Q3HcbXhU1MCOvwh]]></SuiteTicket></xml>

//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		StringReader sr = new StringReader(sMsg);
//		InputSource is = new InputSource(sr);
//		Document document = db.parse(is.toString());
//
//		Element root = document.getDocumentElement();
//		NodeList nodelist1 = root.getElementsByTagName("Content");
//		String Content = nodelist1.item(0).getTextContent();
        return ResponseEntity.ok("success");
    }

    /**
     * 企业微信数据回调
     * @return
     */
    @GetMapping("/data")
    public ResponseEntity wxData(
            @RequestParam("msg_signature") String sign,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr
                                 ) throws AesException {

        log.info(">>> 调用了wxData,sign:{},timestamp:{},nonce:{},echostr:{}", sign, timestamp, nonce, echostr);

        String sToken = WxApiUtil.getWxProperties().getCallDataToken();;
        String sCorpID = "ww2ed1275aedf3d2ce";
        String sEncodingAESKey = WxApiUtil.getWxProperties().getCallDataEncodingAesKey();


        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sEchoStr = wxcpt.VerifyURL(sign, timestamp,
                nonce, echostr);
        log.info("verifyurl echostr: " + sEchoStr);


        return ResponseEntity.ok(sEchoStr);
    }

    /**
     * @return
     */
    @PostMapping("/data")
    public ResponseEntity wxPostData(@RequestParam("msg_signature") String sign,
                                        @RequestParam("timestamp") String timestamp,
                                        @RequestParam("nonce") String nonce,
                                        @RequestBody String xmlBody) throws AesException {
        log.info(">>> 调用了wxPostData,,wxData,sign:{},timestamp:{},nonce:{},xmlBody:{}", sign, timestamp, nonce, xmlBody);
        String sToken = "k3RjfCLXEYPrjEXC0";
        String sCorpID = "ww2ed1275aedf3d2ce";
        String sEncodingAESKey = "tkR2WN8KHcyXSfwzU9kU3bR8gY4f2kWxDcEjHIU748r";


        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        String sMsg = wxcpt.DecryptMsg(sign, timestamp, nonce, xmlBody);
        System.out.println("after decrypt msg: " + sMsg);

        Map xmlToMap = xmlToMap(sMsg);
        System.out.println("xmlToMap: " + xmlToMap);

//        if(xmlToMap.containsKey("SuiteTicket")){
//            // 如果推送的是票据信息就更新票据
//            Suite suite = new Suite();
//            suite.setSuiteId(xmlToMap.get("SuiteId").toString());
//            suite.setSuiteTicket(xmlToMap.get("SuiteTicket").toString());
//
//            suiteService.updateById(suite);
//
//        } else if(xmlToMap.containsKey("AuthCode")) {
//            // 如果更新的是临时授权码,获取永久授权码
//            String authCode = xmlToMap.get("AuthCode").toString();
//            suiteConstant.savePermanentCode(authCode, xmlToMap.get("SuiteId").toString());
//        }

        return ResponseEntity.ok("success");
    }

    public static void main(String[] args) throws AesException {
        // 代开发应用模板信息 回调配置中的Token
        String sToken = "mmnkv3fSPIPyIr5j1slxIvg";
        // 通用参数中CorpID
        String sCorpID = "ww67ff055de7bf9ee4";
        // 代开发应用模板信息 回调配置中的EncodingAESKey
        String sEncodingAESKey = "6v0KlKwFtLtr2P4OaQmVZpzMfkpFw944wpkYOVUf39k";
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

//        wxcpt.DecryptMsg(sign, timestamp, nonce, xmlBody);
        String sEchoStr = wxcpt.DecryptMsg("e631a8ac40163c67d84d7612ecae272c3ba51943", "1687367390",
                "1687646957", "<xml><ToUserName><![CDATA[wpdGQHEgAAdXDjT5q4nOYyKTjkPjfgAg]]></ToUserName><Encrypt><![CDATA[Ffn/HHjFDWjw4HD6aszoYHcy5756KbA/CfwyK+fUMN8/lzRYSpo/DZxrl+mTo0jDnLPQ500KJevnCML2iktmNeBzqgPbqtBJLcBb4AB/2RD6P9A3tiSPd4ZeYP/mgppZhzCuLXdkyeNq7ACDOi9foZOsSFRX5OAr4erXBDrO2zKpuTcm22TPEsn1ZdNWRIuvRnsDD1ZioQWMzd1+fvOaSZEfeUBR4qiAbqaCndad1Do5BOoT+k6FSLMJiTFeD9zh3b4UwFFDcpaTEziIz+VohTrQDKkD7crd4Dn+hHt1nxwjAdm0Jh3FJgyVmL9FipDoONkFS76CQKyPeEEi1vyBIVKZpYMEfm7ckchWPOr2KgN7CUd4DI2sqR1FtoOi+lknTao8tBY5h4tDIhj50+vyE/DdhqdTs4Y6GjBEPkXK+G/pNkpj5+KrkCFwQTXoid1o/xnZuM1cee3/duiHW2aApw==]]></Encrypt><AgentID><![CDATA[1000007]]></AgentID></xml>");
//        String sMsg = wxcpt.DecryptMsg(sign, timestamp, nonce, xmlBody);

        System.out.println("verifyurl echostr: " + sEchoStr);
    }


    /**
     * 企业微信数据回调
     * @return
     */
    @GetMapping("/system")
    public ResponseEntity wxSystem(
            @RequestParam("msg_signature") String sign,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr
    ) throws AesException {

        log.info(">>> wxSystem,sign:{},timestamp:{},nonce:{},echostr:{}", sign, timestamp, nonce, echostr);


        String sToken = "G9aYL93aV9UEBpeqpO3";
        String sCorpID = "ww2ed1275aedf3d2ce";
        String sEncodingAESKey = "xoxfVNAtuHw0JF42kPTAWPNgxVydKx1FcjZubo1ntt6";


        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sEchoStr = wxcpt.VerifyURL(sign, timestamp,
                nonce, echostr);
        log.info("verifyurl echostr: " + sEchoStr);


        return ResponseEntity.ok(sEchoStr);
    }

    /**
     * @return
     */
    @GetMapping("/command")
    public ResponseEntity wxCommand(@RequestParam("msg_signature") String sign,
                                    @RequestParam("timestamp") String timestamp,
                                    @RequestParam("nonce") String nonce,
                                    @RequestParam("echostr") String echostr) throws AesException {
        log.info(">>> 调用了wxCommand");
        String sToken = WxApiUtil.getWxProperties().getCallDataToken();
        String sCorpID = "ww2ed1275aedf3d2ce";
        String sEncodingAESKey =WxApiUtil.getWxProperties().getCallDataEncodingAesKey();

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sEchoStr = wxcpt.VerifyURL(sign, timestamp,
                nonce, echostr);
        log.info("verifyurl echostr: " + sEchoStr);


        return ResponseEntity.ok(sEchoStr);
    }

    /**
     * @return
     */
    @PostMapping("/command")
    public ResponseEntity wxPostCommand(@RequestParam("msg_signature") String sign,
                                    @RequestParam("timestamp") String timestamp,
                                    @RequestParam("nonce") String nonce,
                                        @RequestBody String xmlBody) throws AesException {
        log.info(">>> 调用了wxPostCommand,,wxData,sign:{},timestamp:{},nonce:{},xmlBody:{}", sign, timestamp, nonce, xmlBody);
        String sToken = WxApiUtil.getWxProperties().getCallDataToken();
        String sCorpID = WxApiUtil.getWxProperties().getSuiteId();
        String sEncodingAESKey = WxApiUtil.getWxProperties().getCallDataEncodingAesKey();


        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        String sMsg = wxcpt.DecryptMsg(sign, timestamp, nonce, xmlBody);
        System.out.println("after decrypt msg: " + sMsg);

        Map<String,String> xmlToMap = parseXML(sMsg);
        log.info(">>> xmlToMap:{}", xmlToMap);

        if(xmlToMap.containsKey("SuiteTicket")){
            // 如果推送的是票据信息就更新票据
            Suite suite = new Suite();
            suite.setSuiteId(xmlToMap.get("SuiteId"));
            suite.setSuiteTicket(xmlToMap.get("SuiteTicket"));
            suiteService.updateById(suite);
        } else if(xmlToMap.containsKey("AuthCode")) {
            // 如果更新的是临时授权码,获取永久授权码
            String authCode = xmlToMap.get("AuthCode");
            enterpriseService.install(authCode);
        }else if (xmlToMap.containsKey("InfoType")) {   //通用消息
            String infoType = xmlToMap.get("InfoType");
            if (infoType.contains("cancel_auth")) {
                // 如果是授权成功
                String authCorpId = xmlToMap.get("AuthCorpId");
                //企业微信授权成功，更新企业状态
                enterpriseService.update(new UpdateWrapper<Enterprise>().set("status", 0).eq("corp_id", authCorpId));
            }
        }

        return ResponseEntity.ok("success");
    }
    public static Map<String, String> parseXML(String xml) {
        Map<String, String> resultMap = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));
            org.w3c.dom.Document document = builder.parse(inputSource);

            org.w3c.dom.Element rootElement = document.getDocumentElement();
            NodeList nodeList = rootElement.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String tagName = node.getNodeName();
                    String value = getNodeValue(node);
                    resultMap.put(tagName, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    private static String getNodeValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                return childNode.getNodeValue();
            }
        }
        return null;
    }


    private Map<String, String> xmlToMap(String xml){
        Map<String, String> rest = new HashMap<String, String>();
        SAXReader reader = new SAXReader(false);
        Document doc;
        try {
            doc = reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            @SuppressWarnings("unchecked")
            List<Element> els = doc.getRootElement().elements();
            for(Element el : els){
                rest.put(el.getName(), el.getTextTrim());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rest;
    }
}
