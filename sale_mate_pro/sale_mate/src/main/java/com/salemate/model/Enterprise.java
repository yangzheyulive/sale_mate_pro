package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "enterprise")
public class Enterprise implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 企业id
     */
    @TableField(value = "corp_id")
    private String corpId;

    /**
     * 使用的应用id
     */
    @TableField(value = "suite_id")
    private String suiteId;

    /**
     * 企业微信永久授权码
     */
    @TableField(value = "permanent_code")
    private String permanentCode;

    /**
     * 企业名称
     */
    @TableField(value = "corp_name")
    private String corpName;

    /**
     * 企业用户规模
     */
    @TableField(value = "corp_user_max")
    private Integer corpUserMax;

    /**
     * 企业认证类型，认证号：verified, 注册号：unverified所属行业
     */
    @TableField(value = "corp_type")
    private String corpType;

    /**
     * 企业方形图像
     */
    @TableField(value = "corp_round_logo_url")
    private String corpRoundLogoUrl;

    /**
     * 企业圆形图像
     */
    @TableField(value = "corp_square_logo_url")
    private String corpSquareLogoUrl;

    /**
     * 企业性质，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号
     */
    @TableField(value = "subject_type")
    private Integer subjectType;

    /**
     * 企业微信二维码
     */
    @TableField(value = "corp_wxqrcode")
    private String corpWxqrcode;

    /**
     * 企业规模
     */
    @TableField(value = "corp_scale")
    private String corpScale;

    /**
     * 企业所属行业
     */
    @TableField(value = "corp_industry")
    private String corpIndustry;

    /**
     * 企业所属子行业
     */
    @TableField(value = "corp_sub_industry")
    private String corpSubIndustry;

    /**
     * 允许获取通讯录的秘钥
     */
    @TableField(value = "user_secret")
    private String userSecret;
    /**
     * 对外收款密钥
     */
    @TableField(value = "bill_secret")
    private String billSecret;

    /**
     * 允许获取客户的秘钥
     */
    @TableField(value = "customer_secret")
    private String customerSecret;

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
    /**
     * 授权状态 1：已授权 0：未授权
     */
    @TableField(value = "status")
    private Integer status;

    private static final long serialVersionUID = 1L;

    public static final String COL_CORP_ID = "corp_id";

    public static final String COL_SUITE_ID = "suite_id";

    public static final String COL_PERMANENT_CODE = "permanent_code";

    public static final String COL_CORP_NAME = "corp_name";

    public static final String COL_CORP_USER_MAX = "corp_user_max";

    public static final String COL_CORP_TYPE = "corp_type";

    public static final String COL_CORP_ROUND_LOGO_URL = "corp_round_logo_url";

    public static final String COL_CORP_SQUARE_LOGO_URL = "corp_square_logo_url";

    public static final String COL_SUBJECT_TYPE = "subject_type";

    public static final String COL_CORP_WXQRCODE = "corp_wxqrcode";

    public static final String COL_CORP_SCALE = "corp_scale";

    public static final String COL_CORP_INDUSTRY = "corp_industry";

    public static final String COL_CORP_SUB_INDUSTRY = "corp_sub_industry";

    public static final String COL_USER_SECRET = "user_secret";

    public static final String COL_CUSTOMER_SECRET = "customer_secret";

    public static final String COL_CREATE_DATE = "create_date";

    public static final String COL_UPDATE_DATE = "update_date";
}