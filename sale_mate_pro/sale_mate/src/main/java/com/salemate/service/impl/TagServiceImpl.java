package com.salemate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.GroupMapper;
import com.salemate.mapper.TagMapper;
import com.salemate.model.Group;
import com.salemate.model.Tag;
import com.salemate.service.TagService;
import com.salemate.vo.TagListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService{
    @Autowired
    private GroupMapper groupMapper;



    @Override
    public List<TagListVo> listByVo() {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        List<Group> list = groupMapper.selectList(new QueryWrapper<Group>().eq("corp_id", corpId));
        if(list == null || list.size() == 0){
            return new ArrayList<>();
        }
        List<TagListVo> tagListVos = new ArrayList<>();

        for (Group group : list) {
            TagListVo tagListVo = new TagListVo();
            List<Tag> tags = baseMapper.selectList(new QueryWrapper<Tag>().eq("group_id", group.getGroupId()));
            tagListVo.setGroupId(group.getGroupId());
            tagListVo.setGroupName(group.getGroupName());
            tagListVo.setTagList(tags);
            tagListVos.add(tagListVo);
        }
        return tagListVos;
    }
}
