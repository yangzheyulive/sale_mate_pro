package com.salemate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salemate.bo.MaterialSaveBo;
import com.salemate.model.MaterialHistory;
import com.salemate.model.MaterialImage;
import com.salemate.model.MaterialText;

import java.util.List;

public interface MaterialHistoryService extends IService<MaterialHistory> {
    List<MaterialText> queryPage();

    List<MaterialImage> getMaterialByGroupImage(String groupId);

    MaterialText getMaterialByGroupText(String groupId);

    public String getMaterialByGroup(Integer labelId);

    MaterialImage randomImage();

    void saveMaterial(MaterialSaveBo bo);
}
