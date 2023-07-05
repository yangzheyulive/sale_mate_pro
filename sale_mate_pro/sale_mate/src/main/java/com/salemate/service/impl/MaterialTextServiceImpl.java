package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.MaterialTextMapper;
import com.salemate.model.MaterialText;
import com.salemate.service.MaterialTextService;
import org.springframework.stereotype.Service;

@Service
public class MaterialTextServiceImpl extends ServiceImpl<MaterialTextMapper, MaterialText> implements MaterialTextService {
}