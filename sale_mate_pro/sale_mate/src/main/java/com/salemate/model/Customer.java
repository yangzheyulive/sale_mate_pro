package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName(value = "customer")
public class Customer implements Serializable {
    /**
     * 客户id
     */
    @TableId(value = "external_userid", type = IdType.INPUT)
    private String externalUserid;
    /**
     * 企业id
     */
    @TableField(value = "corp_id")
    private String corpId;
    @TableField(value = "avatar")
    private String avatar;
    /**
     * 客户名字
     */
    @TableField(value = "name")
    private String name;
    /**
     * 性别 0-未知 1-男性 2-女性
     */
    @TableField(value = "gender")
    private Integer gender;
    /**
     * 外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
     */
    @TableField(value = "type")
    private Integer type;
    /**
     * 标签多个标签用|分割
     */
    @TableField(value = "tag_name")
    private String tagName;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 添加方式，对照企微列表
     */
    @TableField(value = "add_way")
    private Integer addWay;

    /**
     * 添加人ID
     */
    @TableField(value = "oper_userid")
    private String operUserid;

    /**
     * 添加人name
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 添加客户时间
     */
    @TableField(value = "oper_time")
    private Date operTime;
    /**
     * 添加客户时间
     */
    @TableField(value = "label_type")
    private Integer labelType;

    /**
     * 新增时间
     */
    @TableField(value = "create_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 更新时间
     */
    @TableField(value = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @TableField(value = "user_id")
    private String userId;

}