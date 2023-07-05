package com.salemate.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("corp_group")
@Data
public class Group {
    @TableId("group_id")
    private String groupId;
    @TableField("group_name")
    private String groupName;
    @TableField("corp_id")
    private String corpId;
    @TableField("create_date")
    private Date createDate;
    @TableField("update_date")
    private Date updateDate;

}
