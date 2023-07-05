package com.salemate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.mapper.MaterialImageMapper;
import com.salemate.model.MaterialImage;
import com.salemate.service.MaterialImageService;
import com.salemate.common.util.PageUtils;
import org.springframework.stereotype.Service;

@Service
public class MaterialImageServiceImpl extends ServiceImpl<MaterialImageMapper, MaterialImage> implements MaterialImageService {
    // 可以实现自定义的业务方法


    @Override
    public PageUtils queryPage(Long page, Long pageSize) {
        return null;
    }
}
