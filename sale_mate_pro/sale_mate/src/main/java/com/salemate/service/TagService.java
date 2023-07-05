package com.salemate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salemate.model.Tag;
import com.salemate.vo.TagListVo;

import java.util.List;

public interface TagService extends IService<Tag> {
    List<TagListVo> listByVo();
}
