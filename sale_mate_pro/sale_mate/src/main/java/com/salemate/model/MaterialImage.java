package com.salemate.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("material_image")
public class MaterialImage {
    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(value = "path")
    private String path;

    @TableField(value = "update_date")
    private Date updateDate;

    @TableField(value = "create_date")
    private Date createDate;


    @TableField(value = "count")
    private Long count;
}