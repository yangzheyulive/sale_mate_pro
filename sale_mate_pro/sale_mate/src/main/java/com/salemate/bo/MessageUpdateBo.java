package com.salemate.bo;

import lombok.Data;

import java.util.List;

@Data
public class MessageUpdateBo {
    private String messageId;
    private String textId;
    private String content;
    private String sendTime;
    private String sendDate;
    private Integer labelType;
    private Integer sendType;
    private List<String> imageList;

}
