package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "user")
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private String userId;

    /**
     * 企业id
     */
    @TableField(value = "enterprise_id")
    private Long enterpriseId;

    /**
     * 用户名称
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;


    @TableField(value = "corpId")
    private String corpId;

    /**
     * 性别 0-未知 1-男性 2-女性
     */
    @TableField(value = "gender")
    private Byte gender;

    /**
     * 上次登录code
     */
    @TableField(value = "last_login_code")
    private String lastLoginCode;

    /**
     * 状态  0  第一次扫码登陆  2 初始话中   1 初始化完成
     */
    @TableField(value = "update_state")
    private Byte updateState;

    /**
     * 新增时间
     */
    @TableField(value = "create_date")
    private Date createDate;

    /**
     * 更新时间
     */
    @TableField(value = "update_date")
    private Date updateDate;
}