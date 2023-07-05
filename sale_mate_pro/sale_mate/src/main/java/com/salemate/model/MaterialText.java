package com.salemate.model;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

@TableName("material_text")
public class MaterialText {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField(value = "content")
    private String content;
    @TableField(value = "count")
    private Long count;
    @TableField(value = "create_date")
    private Date createDate;
    @TableField(value = "update_date")
    private Date updateDate;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
// Getters and Setters (omitted for brevity)
}
