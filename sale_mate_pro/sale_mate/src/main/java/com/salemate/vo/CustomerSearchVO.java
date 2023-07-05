package com.salemate.vo;

import lombok.Data;

@Data
public class CustomerSearchVO {
    private String corpid;
    private String name;
    private String remark;
    private String desc;
    private Integer labelType;
    private Integer gender;
    private Integer currentPage;
    private Integer totalSize;
    private Integer pageSize;
}
