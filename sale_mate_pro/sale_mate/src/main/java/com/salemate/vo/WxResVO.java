package com.salemate.vo;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class WxResVO {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("auth_code","ibjOjxdRSYHi6-orywB7Xljl4v-nawF-9MNHYPE1a2ubpixvaiDW08Gvc5Q9gAdYiBa206G3RHu7G8yioO0479AhAM2HkV3vNPkgD8Np0Yk" );
        HttpResponse execute = HttpUtil.createPost("https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=QFpdQGItJ3XA_R8AgaIatN2CZ8DPcI6zbFOETtAj7TFNRrJKrpeed_6VQpqdyvYvWWJAUqP8Gba9e_-RUX01lSbPv7CfsiH6TG1FL6Y06X0ykjkT3Hb6h7xJUGzElhOE")
                .body(jsonObject.toJSONString())
                .contentType("application/json")
                .execute();
        System.out.println(execute.body());
    }

}
