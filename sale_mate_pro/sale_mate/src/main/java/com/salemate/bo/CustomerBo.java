package com.salemate.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class CustomerBo {

        /**
         * 客户id
         */
        private String externalUserid;
        /**
         * 企业id
         */
        private String corpId;


        private String avatar;
        /**
         * 客户名字
         */
        private String name;
        /**
         * 性别 0-未知 1-男性 2-女性
         */
        private Integer gender;
        /**
         * 外部联系人的类型，1表示该外部联系人是微信用户，2表示该外部联系人是企业微信用户
         */
        private Integer type;
        /**
         * 标签多个标签用|分割
         */
        private String tagName;

        /**
         * 备注
         */
        private String remark;

        /**
         * 描述
         */
        private String description;

        /**
         * 添加方式，对照企微列表
         */
        private Integer addWay;

        /**
         * 添加人ID
         */
        private String operUserid;

        /**
         * 添加人name
         */
        private String userName;

        /**
         * 添加客户时间
         */
        private Date operTime;
        /**
         * 添加客户时间
         */
        private Integer labelType;

        /**
         * 新增时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createDate;

        /**
         * 更新时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date updateDate;

        private String userId;

        private List<String> tags;

        private Long pageNum;

        private Long pageSize;


}
