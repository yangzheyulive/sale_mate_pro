package com.salemate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salemate.model.Enterprise;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {
    int updateBatch(List<Enterprise> list);

    int batchInsert(@Param("list") List<Enterprise> list);
}