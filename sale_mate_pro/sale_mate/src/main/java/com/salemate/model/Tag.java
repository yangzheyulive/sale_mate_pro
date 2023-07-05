package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tag")
public class Tag {
    @TableId(value = "tag_id",type = IdType.INPUT)
    private String tagId;
    @TableField("name")
    private String name;
    @TableField("group_id")
    private String groupId;
    @TableField("create_date")
    private Date createDate;
    @TableField("update_date")
    private Date updateDate;
}
