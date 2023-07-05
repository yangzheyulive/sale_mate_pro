package com.salemate.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class MessageVo {
    private String id;
    private String imageId;
    private String textId;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;
    private Integer sendType;
    private Integer status;
}
