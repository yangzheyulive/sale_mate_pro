package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="message_accept")
public class MessageAccept {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;
    @TableField(value = "message_id")
    private String messageId;
    @TableField(value = "external_userid")
    private String externalUserid;
    @TableField(value = "tag_id")
    private String tagId;
    @TableField(value = "label_type")
    private Integer labelType;
}
