package com.salemate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salemate.model.MaterialHistory;
import com.salemate.vo.MaterialHistoryCountVo;
import com.salemate.vo.MaterialListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialHistoryMapper extends BaseMapper<MaterialHistory> {
    List<MaterialListVo> selectListByMaterialVo();

    List<MaterialHistoryCountVo> selectCountByGroupId( Integer labelType);
    // 可以添加自定义的SQL操作方法
}
