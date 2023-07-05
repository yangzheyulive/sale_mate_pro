package com.salemate.bo;

import lombok.Data;

@Data
public class MaterialListBo {
    private Integer labelType;
    private Long pageSize;
    private Long pageNum;
    private Integer sendType;
}
