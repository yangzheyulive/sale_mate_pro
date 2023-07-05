package com.salemate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salemate.model.MaterialImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialImageMapper extends BaseMapper<MaterialImage> {
    // 可以添加自定义的SQL操作方法
}
