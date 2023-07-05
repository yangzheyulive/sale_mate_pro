package com.salemate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salemate.model.MaterialImage;
import com.salemate.common.util.PageUtils;

public interface MaterialImageService extends IService<MaterialImage> {

    PageUtils queryPage(Long page, Long pageSize);
    // 可以添加自定义的业务方法
}
