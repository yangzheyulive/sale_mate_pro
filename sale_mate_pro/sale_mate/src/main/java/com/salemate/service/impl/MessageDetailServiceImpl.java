package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.MessageDetailMapper;
import com.salemate.model.MessageDetail;
import com.salemate.service.MessageDetailService;
import org.springframework.stereotype.Service;

@Service
public class MessageDetailServiceImpl extends ServiceImpl<MessageDetailMapper, MessageDetail> implements MessageDetailService {
}
