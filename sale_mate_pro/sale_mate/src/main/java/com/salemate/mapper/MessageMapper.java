package com.salemate.mapper;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.salemate.model.Message;
import com.salemate.vo.MessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    List<MessageVo> selectUserMessage(@Param("corpId") String corpId,@Param("externalUserid") String externalUserid, @Param("start") Date start,@Param("end") DateTime end,@Param("labelType") Integer labelType);
    // 可以添加自定义的SQL操作方法
}
