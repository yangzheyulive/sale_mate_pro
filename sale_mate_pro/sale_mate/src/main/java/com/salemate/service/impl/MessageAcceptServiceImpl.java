package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.MessageAcceptMapper;
import com.salemate.model.MessageAccept;
import com.salemate.service.MessageAcceptService;
import org.springframework.stereotype.Service;

@Service
public class MessageAcceptServiceImpl extends ServiceImpl<MessageAcceptMapper, MessageAccept> implements MessageAcceptService {
}
