package com.salemate.vo;

import lombok.Data;

@Data
public class CustomerSyncVo {
    private String corpId;
    //1 同步中 -1 同步失败 0 同步成功
    private Integer status;
    //同步数量
    private Long count;
    //同步失败原因
    private String msg;
}
