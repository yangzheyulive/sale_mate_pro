package com.salemate.bo;

import lombok.Data;

import java.util.List;

@Data
public class MaterialSaveBo {
    private String groupId;
    private String content;
    private String images;
    private String textId;
    private List<String> imagesIds;
    private Integer  labelType;
    private Integer sendType;
}
