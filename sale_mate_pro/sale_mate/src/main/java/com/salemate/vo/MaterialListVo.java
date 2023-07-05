package com.salemate.vo;

import lombok.Data;

@Data
public class MaterialListVo {
    /**
     * 素材库id
     */
    private String id;
    /**
     * 图片id
     */
    private String imageId;
    /**
     * 素材名称
     */
    private String groupId;
    /**
     * 素材类型
     */
    private Integer type;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 发送类型
     */
    private Integer sendType;
    /**
     * 标签类型
     */
    private Integer labelType;

}
