package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.GroupMapper;
import com.salemate.model.Group;
import com.salemate.service.GroupService;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {
}
