package com.salemate.vo;

import lombok.Data;

@Data
public class WxAuthVO {
    /**
     * 登录返回code
     */
    private String code;

    /**
     * 登录返回state
     */
    private String state;

    /**
     * 企业id
     */

    private String corpid;


    /**
     * 企业秘钥
     */
    private String corpsecret;

}
