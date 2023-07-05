package com.salemate.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("pay_bill")
@Data
public class PayBill {
    /**
     *   `transaction_id` varchar(255) NOT NULL,
     *   `bill_type` int(11) DEFAULT NULL,
     *   `trade_state` int(11) DEFAULT NULL,
     *   `payee_userid` varchar(255) DEFAULT NULL,
     *   `external_userid` varchar(255) DEFAULT NULL,
     */
    @TableId(value = "transaction_id",type= IdType.INPUT)
    private String transactionId;
    @TableField("bill_type")
    private Integer billType;
    @TableField("trade_state")
    private Integer tradeState;
    @TableField("payee_userid")
    private String payeeUserid;
    @TableField("external_userid")
    private String externalUserid;
}
