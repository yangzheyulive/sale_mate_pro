package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("customer_tag")
@Data
public class CustomerTag {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;
    @TableField("customer_id")
    private String customerId;
    @TableField("tag_id")
    private String tagId;
}
