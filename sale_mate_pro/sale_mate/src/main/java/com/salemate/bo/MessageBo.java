package com.salemate.bo;

import lombok.Data;

@Data
public class MessageBo {
    private String corpId;
    private String sendTime;
    private Integer sendType;
    private Integer labelType;
    private String messageId;
}
