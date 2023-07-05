package com.salemate.vo;

import lombok.Data;

import java.util.List;
@Data
public class MaterialItemVo {
        private String groupId;
        private String content;
        private String textId;
        private List<String> imagesIds;
        private Integer  labelType;
        private Integer sendType;
        private Integer fgrs;
        private Integer cjl;
        private Integer hxl;


}
