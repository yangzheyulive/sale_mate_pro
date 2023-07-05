package com.salemate.vo;

import com.salemate.model.Tag;
import lombok.Data;

import java.util.List;

@Data
public class TagListVo {
    private String groupId;
    private String groupName;
    List<Tag> tagList;
}
