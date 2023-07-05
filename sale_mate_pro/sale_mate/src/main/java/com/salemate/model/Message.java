package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 发布消息 （朋友圈/用户私信）
 */
@TableName("message")
@Data
public class Message {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "job_id")
    private Long jobId;

    @TableField(value = "corp_id")
    private String corpId;

    @TableField(value = "send_type")
    private Integer sendType;

    @TableField(value = "send_date")
    private Date sendDate;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "update_date")
    private Date updateDate;

    @TableField(value = "status")
    private Integer status;
}
