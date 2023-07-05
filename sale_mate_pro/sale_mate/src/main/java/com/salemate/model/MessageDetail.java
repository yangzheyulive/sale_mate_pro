package com.salemate.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName("message_detail")
@Data
public class MessageDetail {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("message_id")
    private String messageId;

    @TableField("image_id")
    private String imageId;

    @TableField("text_id")
    private String textId;

    @TableField("user_id")
    private String userId;

    @TableField("type")
    private Integer type;

    @TableField("send_type")
    private Integer sendType;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "update_date")
    private Date updateDate;

    // Getters and Setters (略去)

    // toString() 方法 (略去)
}
