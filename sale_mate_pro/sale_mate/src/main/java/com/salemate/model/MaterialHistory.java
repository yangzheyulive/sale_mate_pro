package com.salemate.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@TableName("material_history")
@Data
public class MaterialHistory {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(value = "group_id")
    private String groupId;

    @TableField(value = "text_id")
    private String textId;

    @TableField(value = "image_id")
    private String ImageId;

    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "corp_id")
    private String corpId;
    @TableField(value = "send_type")
    private Integer sendType;

    @TableField(value = "label_type")
    private Integer labelType;

    @TableField(value = "type" )
    private Integer type;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "update_date")
    private Date updateDate;

    // getter and setter methods
}