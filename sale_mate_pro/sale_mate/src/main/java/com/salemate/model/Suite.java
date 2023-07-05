package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "suite")
public class Suite implements Serializable {
    /**
     * 应用主键
     */
    @TableId(value = "suite_id", type = IdType.INPUT)
    private String suiteId;

    /**
     * 应用秘钥
     */
    @TableField(value = "suite_secret")
    private String suiteSecret;

    /**
     * 应用票据 微信中每10分钟更新一次
     */
    @TableField(value = "suite_ticket")
    private String suiteTicket;

    /**
     * 登录的suite_id
     */
    @TableField(value = "login_suite_id")
    private String loginSuiteId;

    /**
     * 登录的秘钥
     */
    @TableField(value = "login_suite_ticket")
    private String loginSuiteTicket;

    /**
     * 登录的票据
     */
    @TableField(value = "login_suite_secret")
    private String loginSuiteSecret;

    /**
     * 创建时间
     */
    @TableField(value = "create_date")
    private Date createDate;

    /**
     * 上次更新时间
     */
    @TableField(value = "update_date")
    private Date updateDate;

    private static final long serialVersionUID = 1L;

    public static final String COL_SUITE_ID = "suite_id";

    public static final String COL_SUITE_SECRET = "suite_secret";

    public static final String COL_SUITE_TICKET = "suite_ticket";

    public static final String COL_LOGIN_SUITE_ID = "login_suite_id";

    public static final String COL_LOGIN_SUITE_TICKET = "login_suite_ticket";

    public static final String COL_LOGIN_SUITE_SECRET = "login_suite_secret";

    public static final String COL_CREATE_DATE = "create_date";

    public static final String COL_UPDATE_DATE = "update_date";
}