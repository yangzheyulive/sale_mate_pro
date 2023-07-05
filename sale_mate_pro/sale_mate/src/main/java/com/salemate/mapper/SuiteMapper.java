package com.salemate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salemate.model.Suite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SuiteMapper extends BaseMapper<Suite> {
    int updateBatch(List<Suite> list);

    int batchInsert(@Param("list") List<Suite> list);
}