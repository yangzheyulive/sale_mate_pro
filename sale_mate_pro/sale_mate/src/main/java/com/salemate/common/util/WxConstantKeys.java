package com.salemate.common.util;

public class WxConstantKeys {
    public static final String SUITE_ACCESS_TOKEN = "SUITE_ACCESS_TOKEN";

    public static final String SUITE_TICKET = "SUITE_TICKET";
    //客户公司token
    public static final String CUSTOMER_ACCESS_TOKEN = "CUSTOMER_ACCESS_TOKEN_";
    public static final String USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN_";
    public static final String CORP_ACCESS_TOKEN = "CORP_ACCESS_TOKEN_";
    public static final String CORP_PAY_TOKEN = "CORP_PAY_TOKEN_";

    /**
     * 定时任务状态
     *
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 购买状态
     */
    public enum LabelType {
        /**
         * 未购买
         */
        NO_Buy(0),
        /**
         * 促复购
         */
        Buy(1),
        /**
         * 转介绍
         */
        INTRODUCE(2);

        private int value;

        LabelType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 素材类型
     */
    public enum MaterialType {
        /**
         * 图片
         */
        IMAGE(1),
        /**
         * 文本
         */
        TEXT(0);

        private int value;

        MaterialType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    /**
     * 发送类型
     */
    public enum SEND_TYPE {
        /**
         * 群发
         */
        MSG(1),
        /**
         * 朋友圈
         */
        CIRCLE(0);

        private int value;

        SEND_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}