package com.salemate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.bo.MaterialSaveBo;
import com.salemate.mapper.MaterialHistoryMapper;
import com.salemate.mapper.MaterialImageMapper;
import com.salemate.mapper.MaterialTextMapper;
import com.salemate.model.MaterialHistory;
import com.salemate.model.MaterialImage;
import com.salemate.model.MaterialText;
import com.salemate.service.MaterialHistoryService;
import com.salemate.common.util.WxConstantKeys;
import com.salemate.vo.MaterialHistoryCountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MaterialHistoryServiceImpl extends ServiceImpl<MaterialHistoryMapper, MaterialHistory> implements MaterialHistoryService {
    @Autowired
    private MaterialImageMapper materialImageMapper;
    @Autowired
    private MaterialTextMapper materialTextMapper;

    @Override
    public List<MaterialText> queryPage() {
        String userId = StpUtil.getLoginIdAsString();
        String corpId = StpUtil.getTokenSession().getString("corpId");
        Wrapper<MaterialHistory> query = lambdaQuery().eq(MaterialHistory::getUserId, userId)
                .eq(MaterialHistory::getCorpId, corpId)
                .eq(MaterialHistory::getType, WxConstantKeys.MaterialType.TEXT.getValue())
                .orderByDesc(MaterialHistory::getCreateDate);
        List<MaterialHistory> list = list(query);
        List<String> textId = CollUtil.getFieldValues(list, "textId", String.class);
        return materialTextMapper.selectBatchIds(textId);
    }

    @Override
    public List<MaterialImage> getMaterialByGroupImage(String groupId) {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        List<MaterialHistory> list = lambdaQuery().eq(MaterialHistory::getGroupId, groupId)
                .eq(MaterialHistory::getCorpId, corpId)
                .eq(MaterialHistory::getType, WxConstantKeys.MaterialType.IMAGE.getValue())
                .list();
        if (list == null || list.size() == 0) {
            return null;
        }
        List<String> imageIds = CollUtil.getFieldValues(list, "ImageId", String.class);
        return materialImageMapper.selectBatchIds(imageIds);
    }

    @Override
    public MaterialText getMaterialByGroupText(String groupId) {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        MaterialHistory materialHistory = lambdaQuery().eq(MaterialHistory::getGroupId, groupId)
                .eq(MaterialHistory::getCorpId, corpId)
                .eq(MaterialHistory::getType, WxConstantKeys.MaterialType.TEXT.getValue())
                .one();
        if (materialHistory == null ) {
            return null;
        }
        String textId = materialHistory.getTextId();
        return materialTextMapper.selectById(textId);
    }

    @Override
    public String getMaterialByGroup(Integer labelId) {
        List<MaterialHistoryCountVo> buyCountVos = baseMapper.selectCountByGroupId(labelId);
        if (CollUtil.isEmpty(buyCountVos)) {
            log.info("素材库为空，即时添加素材！！！");
            throw new RuntimeException("素材库为空，即时添加素材！！！");
        }
        log.info("buyCountVos:{}", buyCountVos);
        //随机获取一条消息
        int index1 = RandomUtil.randomInt(0, buyCountVos.size());
        return buyCountVos.get(index1).getGroupId();
    }

    @Override
    public MaterialImage randomImage() {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        List<MaterialHistory> list = lambdaQuery().eq(MaterialHistory::getCorpId, corpId)
                .eq(MaterialHistory::getType, WxConstantKeys.MaterialType.IMAGE.getValue())
                .list();
        if (list == null || list.size() == 0) {
            throw new RuntimeException("素材库为空，即时添加素材！！！");
        }
        List<String> imageIds = CollUtil.getFieldValues(list, "ImageId", String.class);
        int index = RandomUtil.randomInt(0, imageIds.size());
        return materialImageMapper.selectById(imageIds.get(index));
    }

    @Override
    public void saveMaterial(MaterialSaveBo bo) {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        Object userId = StpUtil.getLoginId();
        if (bo == null){
            throw new RuntimeException("参数不能为空");
        }
        if (StrUtil.isBlank(bo.getGroupId())) {
            if (bo.getLabelType() == null) {
                throw new RuntimeException("标签类型不能为空");
            }
            if (StrUtil.isBlank(bo.getContent())){
                throw new RuntimeException("内容不能为空");
            }
            if (bo.getSendType() == null){
                throw new RuntimeException("发送类型不能为空");
            }
            if (bo.getImagesIds().size() > 9){
                throw new RuntimeException("图片不能超过9张");
            }
            //新增
            MaterialText materialText = new MaterialText();
            materialText.setContent(bo.getContent());
            materialTextMapper.insert(materialText);
            String uuid = IdUtil.fastSimpleUUID();
            for (String imagesId : bo.getImagesIds()) {
                MaterialHistory materialHistory = new MaterialHistory();
                materialHistory.setImageId(imagesId);
                materialHistory.setGroupId(uuid);
                materialHistory.setType(WxConstantKeys.MaterialType.IMAGE.getValue());
                materialHistory.setLabelType(bo.getLabelType());
                materialHistory.setSendType(bo.getSendType());
                materialHistory.setCorpId(corpId);
                materialHistory.setUserId(userId.toString());
                save(materialHistory);
            }
            MaterialHistory materialHistory = new MaterialHistory();
            materialHistory.setTextId(materialText.getId());
            materialHistory.setGroupId(uuid);
            materialHistory.setType(WxConstantKeys.MaterialType.TEXT.getValue());
            materialHistory.setLabelType(bo.getLabelType());
            materialHistory.setSendType(bo.getSendType());
            materialHistory.setCorpId(corpId);
            materialHistory.setUserId(userId.toString());
            save(materialHistory);
        }else{
            if (bo.getLabelType() == null) {
                throw new RuntimeException("标签类型不能为空");
            }
            if (StrUtil.isBlank(bo.getTextId() )) {
                throw new RuntimeException("标签类型不能为空");
            }
            if (StrUtil.isBlank(bo.getContent())){
                throw new RuntimeException("内容不能为空");
            }
            if (bo.getSendType() == null){
                throw new RuntimeException("发送类型不能为空");
            }
            if (bo.getImagesIds().size() > 9){
                throw new RuntimeException("图片不能超过9张");
            }
            //修改
            MaterialText materialText = materialTextMapper.selectById(bo.getTextId());
            materialText.setContent(bo.getContent());
            materialTextMapper.updateById(materialText);
            //删除图片
            remove(new QueryWrapper<MaterialHistory>().eq("type",WxConstantKeys.MaterialType.IMAGE.getValue())
                    .eq("group_id",bo.getGroupId()));
            for (String imagesId : bo.getImagesIds()) {
                MaterialHistory materialHistory = new MaterialHistory();
                materialHistory.setImageId(imagesId);
                materialHistory.setGroupId(bo.getGroupId());
                materialHistory.setType(WxConstantKeys.MaterialType.IMAGE.getValue());
                materialHistory.setLabelType(bo.getLabelType());
                materialHistory.setSendType(bo.getSendType());
                materialHistory.setCorpId(corpId);
                materialHistory.setUserId(userId.toString());
                save(materialHistory);
            }
        }
    }

}
