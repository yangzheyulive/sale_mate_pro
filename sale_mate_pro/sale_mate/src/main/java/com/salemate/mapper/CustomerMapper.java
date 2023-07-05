package com.salemate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salemate.bo.CustomerBo;
import com.salemate.model.Customer;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    int updateBatch(List<Customer> list);

    int batchInsert(@Param("list") List<Customer> list);

    Page<Customer> queryPage(Page page, @Param("bo") CustomerBo bo);
}