package com.salemate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salemate.model.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
