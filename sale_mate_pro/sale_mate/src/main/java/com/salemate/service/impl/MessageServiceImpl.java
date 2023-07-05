package com.salemate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.bo.MessageUpdateBo;
import com.salemate.common.util.ScheduleUtils;
import com.salemate.common.util.WxApiUtil;
import com.salemate.common.util.WxConstantKeys;
import com.salemate.mapper.*;
import com.salemate.model.*;
import com.salemate.service.MessageService;
import com.salemate.vo.MaterialHistoryCountVo;
import com.salemate.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    // 可以实现自定义的业务方法
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private MaterialHistoryMapper materialHistoryMapper;
    @Autowired
    private MaterialTextMapper materialTextMapper;
    @Autowired
    private MaterialImageMapper materialImageMapper;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private MessageDetailMapper messageDetailMapper;
    @Autowired
    private MessageAcceptMapper messageAcceptMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private TagMapper tagMapper;
    @Override
    public void sendMsg(String messageId) throws IOException {
        // 可以实现自定义的业务方法
        Message message = getById(messageId);
        String corpId = message.getCorpId();
        Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().lambda()
                .eq(Enterprise::getCorpId, corpId).eq(Enterprise::getSuiteId, WxApiUtil.getWxProperties().getSuiteId()));
        //获取要发送的对象
        List<MessageAccept> messageAccepts = messageAcceptMapper.selectList(new QueryWrapper<MessageAccept>().lambda().eq(MessageAccept::getMessageId, messageId));
        if (CollUtil.isEmpty(messageAccepts)) {
            log.info(">>>>> 没有要发送的对象");
            return;
        }
        List<String> externalUserid = CollUtil.getFieldValues(messageAccepts, "externalUserid", String.class);
        List<MessageDetail> messageDetailsImage = messageDetailMapper.selectList(new QueryWrapper<MessageDetail>().lambda().eq(MessageDetail::getMessageId, messageId)
                .eq(MessageDetail::getType, WxConstantKeys.MaterialType.IMAGE.getValue()));
        MessageDetail messageDetailsText = messageDetailMapper.selectOne(new QueryWrapper<MessageDetail>().lambda().eq(MessageDetail::getMessageId, messageId)
                .eq(MessageDetail::getType, WxConstantKeys.MaterialType.TEXT.getValue()));
        if (messageDetailsText == null) {
            throw new RuntimeException("消息内容不存在");
        }
        String textId = messageDetailsText.getTextId();
        MaterialText materialText = materialTextMapper.selectById(textId);
        if (materialText == null) {
            throw new RuntimeException("消息内容不存在");
        }
        //获取图片
        List<String> imageIds = CollUtil.getFieldValues(messageDetailsImage, "imageId", String.class);
        List<MaterialImage> images = materialImageMapper.selectBatchIds(imageIds);
        List<String> mediaIds = new ArrayList<>();
        //读取图片并且上传到企业微信零时接口
        for (MaterialImage image : images) {
            File file = new File(image.getPath());
            byte[] bytes = FileUtil.readBytes(file);
            JSONObject res = WxApiUtil.uploadTemp(bytes, file.getName(), enterprise.getCorpId(), enterprise.getPermanentCode());
            log.info(">>>>> 临时素材:{}",res);
            log.info(">>>>> 临时素材:{}", res);
            String media_id = res.getString("media_id");
            mediaIds.add(media_id);
        }
        //发送消息
        String corpToken = WxApiUtil.getCorpToken(enterprise.getCorpId(), enterprise.getPermanentCode());

        WxApiUtil.addMsgTemplate(corpToken, externalUserid, materialText.getContent(), mediaIds);
        message.setStatus(1);
        updateById(message);
    }

    @Override
    public void sendFriend(String messageId) throws IOException {
        Message one = getOne(new QueryWrapper<Message>().lambda().eq(Message::getId, messageId)
                .eq(Message::getSendType, WxConstantKeys.SEND_TYPE.CIRCLE));
        String corpId = one.getCorpId();
        if (one == null) {
            throw new RuntimeException("消息不存在");
        }
        Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().eq("corp_id",corpId).eq("suite_id",WxApiUtil.getWxProperties().getSuiteId()));
        List<MessageAccept> messageAccepts = messageAcceptMapper.selectList(new QueryWrapper<MessageAccept>().lambda().eq(MessageAccept::getMessageId, messageId));

        JSONObject messageContent = getMessageContent(messageId);
        //查询企业标签组
        Group group = groupMapper.selectOne(new QueryWrapper<Group>().lambda().eq(Group::getCorpId, corpId).eq(Group::getGroupName, "SALEMATE标签组"));
        //查询企业标签
        List<Tag> tags = tagMapper.selectList(new QueryWrapper<Tag>().lambda().eq(Tag::getGroupId, group.getGroupId()));

        List<String> tagIds = new ArrayList<>();
        for (MessageAccept messageAccept : messageAccepts) {

                if (messageAccept.getLabelType().equals(WxConstantKeys.LabelType.NO_Buy.getValue())) {
                    Tag noBuy = CollUtil.findOne(tags, (data) -> data.getName().equals("未购买"));
                    tagIds.add(noBuy.getTagId());
                }
                if (messageAccept.getLabelType().equals(WxConstantKeys.LabelType.Buy.getValue())) {
                    Tag buy = CollUtil.findOne(tags, (data) -> data.getName().equals("促复购"));
                    tagIds.add(buy.getTagId());
                }
                if (messageAccept.getLabelType().equals(WxConstantKeys.LabelType.INTRODUCE.getValue())) {
                    Tag introduce = CollUtil.findOne(tags, (data) -> data.getName().equals("转介绍"));
                    tagIds.add(introduce.getTagId());
                }
        }

        String content = messageContent.getString("text");
        List<MaterialImage> images = messageContent.getJSONArray("images").toJavaList(MaterialImage.class);
        log.info(">>>>> 发送朋友圈:{}", messageContent);
        //上传
        String corpToken = WxApiUtil.getCorpToken(enterprise.getCorpId(), enterprise.getPermanentCode());
        List<String> mediaIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(images)) {
            //读取图片并且上传到企业微信零时接口
            for (MaterialImage image : images) {
                File file = new File(image.getPath());
                log.info(">>>>> 临时素材:{}", file.getName());
                log.info(">>>>> 临时素材:{}", file.exists());
                if (file.exists()) {
                    byte[] bytes1 = compressImageToByteArray(file);
//                ImgUtil.compress(file, new File("/use/tmp/upload/"+file.getName()), 0.7f);
//                byte[] bytes = FileUtil.readBytes(file);
                    JSONObject res = WxApiUtil.uploadAttachment(bytes1, file.getName(), corpToken);
                    log.info(">>>>> 临时素材:{}", res);
                    String media_id = res.getString("media_id");
                    mediaIds.add(media_id);
                }

            }
        }
        log.info(">>>>> 素材IDs:{}", mediaIds);
        JSONObject jsonObject = WxApiUtil.addMomentTask(corpToken, tagIds, content, mediaIds);
        log.info(">>>>> 发送朋友圈:{}", jsonObject);
        one.setStatus(1);
        updateById(one);
    }

    public static byte[] compressImageToByteArray(File inputFile) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputFile);
        int targetWidth = (int) (inputImage.getWidth()  / 2.0);
        int targetHeight = (int) (inputImage.getHeight() / 2.0);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(inputImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String formatName = inputFile.getName().substring(inputFile.getName().lastIndexOf('.') + 1);
        ImageIO.write(outputImage, formatName, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private JSONObject getMessageContent(String messageId) {
        MessageDetail messageText = messageDetailMapper.selectOne(new QueryWrapper<MessageDetail>().lambda()
                .eq(MessageDetail::getMessageId, messageId)
                .eq(MessageDetail::getType, WxConstantKeys.MaterialType.TEXT.getValue()));
        List<MessageDetail> messageImage = messageDetailMapper.selectList(new QueryWrapper<MessageDetail>().lambda()
                .eq(MessageDetail::getMessageId, messageId)
                .eq(MessageDetail::getType, WxConstantKeys.MaterialType.IMAGE.getValue()));
        MaterialText materialText = materialTextMapper.selectById(messageText.getTextId());
        List<MaterialImage> materialImages = materialImageMapper.selectBatchIds(CollUtil.getFieldValues(messageImage, "imageId", String.class));

        //返回{text:xxx,imageIds:xxx}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", materialText.getContent());
        jsonObject.put("images", materialImages);
        return jsonObject;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMessageTask(String corpId, String sendTime, Integer sendType, Integer labelType) {
        //获取要发送的消息
        MaterialHistoryCountVo buyListHis = null;
        //随机获取一条消息
        List<MaterialHistoryCountVo> buyCountVos = materialHistoryMapper.selectCountByGroupId(labelType);
        if (CollUtil.isEmpty(buyCountVos)) {
            log.info("素材库为空，即时添加素材！！！");
            return;
        }
        log.info("buyCountVos:{}", buyCountVos);
        //随机获取一条消息
        int index1 = RandomUtil.randomInt(0, buyCountVos.size() - 1);
        buyListHis = buyCountVos.get(index1);
        List<Customer> customers1 = customerMapper.selectList(new QueryWrapper<Customer>().eq("corp_id", corpId)
                .eq("label_type", labelType));
        createMessageData(corpId, sendTime, buyListHis.getGroupId(), customers1, sendType, labelType);
        //更新发送次数
//        materialHistoryMapper.update(new MaterialHistory(),new UpdateWrapper<MaterialHistory>().eq("group_id",buyListHis.getGroupId()).set("send_count",buyListHis.getCount()+1));
        List<MaterialHistory> materialHistories = materialHistoryMapper.selectList(new QueryWrapper<MaterialHistory>().eq("group_id", buyListHis.getGroupId()));
        for (MaterialHistory materialHistory : materialHistories) {
            String textId = materialHistory.getTextId();
            String imageId = materialHistory.getImageId();
            if (StrUtil.isNotBlank(textId)) {
                MaterialText materialText = materialTextMapper.selectById(textId);
                materialText.setCount(materialText.getCount() + 1);
                materialTextMapper.updateById(materialText);
            }
            if (StrUtil.isNotBlank(imageId)) {
                MaterialImage materialImage = materialImageMapper.selectById(imageId);
                materialImage.setCount(materialImage.getCount() + 1);
                materialImageMapper.updateById(materialImage);
            }
        }
    }

    private void createMessageData(String corpId, String sendTime, String groupId, List<Customer> customers, Integer sendType, Integer labelType) {
        //创建任务
        String uuid = IdUtil.fastSimpleUUID();

        //消息主体
        Message message = new Message();
        message.setId(uuid);
        message.setCorpId(corpId);
        message.setJobId(-1L);
        message.setSendDate(DateUtil.parse(sendTime, "yyyy-MM-dd HH:mm:ss"));
//        message.setUserId();
        message.setSendType(sendType);
        save(message);
        //消息内容
        List<MaterialHistory> materialHistories = materialHistoryMapper.selectList(new QueryWrapper<MaterialHistory>().eq("group_id", groupId));
        for (MaterialHistory materialHistory : materialHistories) {
            Integer type = materialHistory.getType();
            if (WxConstantKeys.MaterialType.TEXT.getValue() == type) {
                MessageDetail messageDetail = new MessageDetail();
                messageDetail.setMessageId(message.getId());
                messageDetail.setTextId(materialHistory.getTextId());
                messageDetail.setType(WxConstantKeys.MaterialType.TEXT.getValue());
                messageDetailMapper.insert(messageDetail);
            } else {
                MessageDetail messageDetail = new MessageDetail();
                messageDetail.setMessageId(message.getId());
                messageDetail.setImageId(materialHistory.getImageId());
                messageDetail.setType(WxConstantKeys.MaterialType.IMAGE.getValue());
                messageDetailMapper.insert(messageDetail);
            }
        }


        if (sendType == WxConstantKeys.SEND_TYPE.CIRCLE.getValue()) {
            Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().eq("corp_id",corpId).eq("suite_id",WxApiUtil.getWxProperties().getSuiteId()));
            String customerAccessToken = WxApiUtil.getCustomerAccessToken(corpId, enterprise.getCustomerSecret());
            //获取要发送的对象
            JSONObject tagCorpList = WxApiUtil.getTagCorpList(customerAccessToken, null);
            log.info("tagCorpList:{}", tagCorpList);
            JSONArray tag_group = tagCorpList.getJSONArray("tag_group");
            for (int i = 0; i < tag_group.size(); i++) {
                JSONObject groupItem = tag_group.getJSONObject(i);
                String group_name = groupItem.getString("group_name");

                if ("SALEMATE_PAY_STATUS".equals(group_name)) {
                    JSONArray tag = groupItem.getJSONArray("tag");
                    for (int i1 = 0; i1 < tag.size(); i1++) {
                        String name = tag.getJSONObject(i1).getString("name");
                        if ("未购买".equals(name) && labelType == WxConstantKeys.LabelType.NO_Buy.getValue()) {
                            MessageAccept messageAccept = new MessageAccept();
                            messageAccept.setMessageId(message.getId());
                            messageAccept.setTagId(tag.getJSONObject(i1).getString("id"));
                            messageAccept.setLabelType(labelType);
                            messageAcceptMapper.insert(messageAccept);
                            break;
                        }
                        if ("促复购".equals(name) && labelType == WxConstantKeys.LabelType.Buy.getValue()) {
                            MessageAccept messageAccept = new MessageAccept();
                            messageAccept.setMessageId(message.getId());
                            messageAccept.setTagId(tag.getJSONObject(i1).getString("id"));
                            messageAccept.setLabelType(labelType);
                            messageAcceptMapper.insert(messageAccept);
                            break;
                        }
                        if ("转介绍".equals(name) && labelType == WxConstantKeys.LabelType.INTRODUCE.getValue()) {
                            MessageAccept messageAccept = new MessageAccept();
                            messageAccept.setMessageId(message.getId());
                            messageAccept.setTagId(tag.getJSONObject(i1).getString("id"));
                            messageAccept.setLabelType(labelType);
                            messageAcceptMapper.insert(messageAccept);
                            break;
                        }
                    }
                }
            }
        } else {
            for (Customer customer : customers) {
                MessageAccept messageAccept = new MessageAccept();
                messageAccept.setMessageId(message.getId());
                messageAccept.setExternalUserid(customer.getExternalUserid());
                messageAcceptMapper.insert(messageAccept);
            }

        }


    }
    private void createMessageData(String corpId, String sendTime, List<Customer> customers, Integer sendType, Integer labelType,List<String> imageList,String textId) {
        //创建任务
        String uuid = IdUtil.fastSimpleUUID();


        //消息主体
        Message message = new Message();
        message.setId(uuid);
        message.setCorpId(corpId);
        message.setJobId(-1L);
        message.setSendDate(DateUtil.parse(sendTime, "yyyy-MM-dd HH:mm:ss"));
//        message.setUserId();
        message.setSendType(sendType);
        save(message);
        //消息内容
        for (String imageId : imageList) {

                MessageDetail messageDetail = new MessageDetail();
                messageDetail.setMessageId(message.getId());
                messageDetail.setImageId(imageId);
                messageDetail.setType(WxConstantKeys.MaterialType.IMAGE.getValue());
                messageDetailMapper.insert(messageDetail);

        }
        MessageDetail messageDetail = new MessageDetail();
        messageDetail.setMessageId(message.getId());
        messageDetail.setTextId(textId);
        messageDetail.setType(WxConstantKeys.MaterialType.TEXT.getValue());
        messageDetailMapper.insert(messageDetail);
        if (sendType == WxConstantKeys.SEND_TYPE.CIRCLE.getValue()) {
            Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().eq("corp_id",corpId).eq("suite_id",WxApiUtil.getWxProperties().getSuiteId()));
            String customerAccessToken = WxApiUtil.getCustomerAccessToken(corpId, enterprise.getCustomerSecret());
            //获取要发送的对象
            JSONObject tagCorpList = WxApiUtil.getTagCorpList(customerAccessToken, null);
            log.info("tagCorpList:{}", tagCorpList);
            JSONArray tag_group = tagCorpList.getJSONArray("tag_group");
            for (int i = 0; i < tag_group.size(); i++) {
                JSONObject groupItem = tag_group.getJSONObject(i);
                String group_name = groupItem.getString("group_name");

                if ("SALEMATE_PAY_STATUS".equals(group_name)) {
                    JSONArray tag = groupItem.getJSONArray("tag");
                    for (int i1 = 0; i1 < tag.size(); i1++) {
                        String name = tag.getJSONObject(i1).getString("name");
                        if ("未购买".equals(name) && labelType == WxConstantKeys.LabelType.NO_Buy.getValue()) {
                            MessageAccept messageAccept = new MessageAccept();
                            messageAccept.setMessageId(message.getId());
                            messageAccept.setTagId(tag.getJSONObject(i1).getString("id"));
                            messageAccept.setLabelType(labelType);
                            messageAcceptMapper.insert(messageAccept);
                            break;
                        }
                        if ("促复购".equals(name) && labelType == WxConstantKeys.LabelType.Buy.getValue()) {
                            MessageAccept messageAccept = new MessageAccept();
                            messageAccept.setMessageId(message.getId());
                            messageAccept.setTagId(tag.getJSONObject(i1).getString("id"));
                            messageAccept.setLabelType(labelType);
                            messageAcceptMapper.insert(messageAccept);
                            break;
                        }
                        if ("转介绍".equals(name) && labelType == WxConstantKeys.LabelType.INTRODUCE.getValue()) {
                            MessageAccept messageAccept = new MessageAccept();
                            messageAccept.setMessageId(message.getId());
                            messageAccept.setTagId(tag.getJSONObject(i1).getString("id"));
                            messageAccept.setLabelType(labelType);
                            messageAcceptMapper.insert(messageAccept);
                            break;
                        }
                    }
                }
            }
        } else {
            for (Customer customer : customers) {
                MessageAccept messageAccept = new MessageAccept();
                messageAccept.setMessageId(message.getId());
                messageAccept.setExternalUserid(customer.getExternalUserid());
                messageAcceptMapper.insert(messageAccept);
            }

        }


    }

    public static void main(String[] args) {

    }
    @Override
    public List<JSONObject> getList(String corpId, String externalUserid) {
        DateTime start = DateUtil.offsetWeek(new Date(), -1);
        DateTime end = DateUtil.offsetWeek(new Date(), 1);
        Customer customer = customerMapper.selectById(externalUserid);
        //群发消息
        List<MessageVo> messageVos = baseMapper.selectUserMessage(corpId, externalUserid, start, end, null);
        log.info("群发消息 messageVos:{}", messageVos);
        //朋友圈消息
        if (customer.getLabelType() == null) {
            customer.setLabelType(WxConstantKeys.LabelType.NO_Buy.getValue());
        }
        log.info("查询用户 customer:{}", customer);
        List<MessageVo> messageVos2 = baseMapper.selectUserMessage(corpId, null, start, end, customer.getLabelType());
        log.info("朋友圈消息 messageVos2:{}", messageVos2);
        messageVos.addAll(messageVos2);

        List<JSONObject> msgList = new ArrayList<>();
        for (MessageVo messageVo : messageVos) {
            //通过messageVo的id分组
            JSONObject msg = CollUtil.findOne(msgList, (data) ->
                    data.getString("id").equals(messageVo.getId())
            );
            if (msg == null) {
                JSONObject item = new JSONObject();
                item.put("id", messageVo.getId());
                //判断图片Id是否为空
                List<String> imageIds = new ArrayList<>();
                item.put("imageList", imageIds);
                if (StrUtil.isNotBlank(messageVo.getImageId())) {
                    imageIds.add(messageVo.getImageId());
                } else {
                    item.put("content", messageVo.getContent());
                    item.put("textId", messageVo.getTextId());
                }
                item.put("sendDate", DateUtil.format(messageVo.getSendDate(), "yyyy-MM-d"));
                item.put("sendTime", DateUtil.format(messageVo.getSendDate(), "HH:mm:ss"));
                item.put("sendType", messageVo.getSendType());
                item.put("status", messageVo.getStatus());
                msgList.add(item);
            } else {
                //判断图片Id是否为空
                if (StrUtil.isNotBlank(messageVo.getImageId())) {
                    List<String> imageList = msg.getJSONArray("imageList")
                            .toJavaList(String.class);
                    imageList.add(messageVo.getImageId());
                    msg.put("imageList", imageList);
                } else {
                    msg.put("content", messageVo.getContent());
                    msg.put("textId", messageVo.getTextId());
                }

            }
        }
        //按照发送时间倒序
        msgList.sort((o1, o2) -> {
            String sendDate1 = o1.getString("sendDate");
            String sendTime1 = o1.getString("sendTime");
            String sendDate2 = o2.getString("sendDate");
            String sendTime2 = o2.getString("sendTime");
            String date1 = sendDate1 + " " + sendTime1;
            String date2 = sendDate2 + " " + sendTime2;
            return DateUtil.compare( DateUtil.parse(date1, "yyyy-MM-dd HH:mm:ss"),DateUtil.parse(date2, "yyyy-MM-dd HH:mm:ss"));
        });
        return msgList;

    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMessage(MessageUpdateBo bo) {
        if (StrUtil.isBlank(bo.getSendDate()) ) {
            throw new RuntimeException("发送日期不能为空");
        }
        if (StrUtil.isBlank(bo.getSendTime())) {
            throw new RuntimeException("发送时间不能为空");
        }
        String sendTime =  bo.getSendDate() + " "+ bo.getSendTime();
        //发送时间不能小于当前时间
        if (DateUtil.parse(sendTime,"yyyy-MM-dd HH:mm:ss").getTime() < System.currentTimeMillis()) {
            throw new RuntimeException("发送时间不能小于当前时间");
        }
        if (bo.getSendType() == null){
            throw new RuntimeException("发送类型不能为空");
        }
        if (bo.getLabelType() == null) {
            throw new RuntimeException("标签类型不能为空");
        }
        if(StrUtil.isBlank(bo.getTextId())){
            throw new RuntimeException("文本内容不能为空");
        }
        if (StrUtil.isBlank(bo.getMessageId())){
            throw new RuntimeException("消息id不能为空");
        }

        //把之前的消息删除重新构建消息
        String corpId = StpUtil.getTokenSession().getString("corpId");
        String messageId = bo.getMessageId();
        Message message = baseMapper.selectById(messageId);
        messageDetailMapper.delete(new QueryWrapper<MessageDetail>().eq("message_id", messageId));
        messageAcceptMapper.delete(new QueryWrapper<MessageAccept>().eq("message_id", messageId));
        ScheduleUtils.deleteScheduleJob(scheduler, message.getJobId());
        removeById(messageId);
        MaterialText materialText = materialTextMapper.selectById(bo.getTextId());
        materialText.setContent(bo.getContent());
        materialTextMapper.updateById(materialText);
        List<Customer> customersList = customerMapper.selectList(new QueryWrapper<Customer>().eq("corp_id", corpId)
                .eq("label_type", bo.getLabelType()));
        //创新消息
        createMessageData(corpId,sendTime,customersList,bo.getSendType(),bo.getLabelType(),bo.getImageList(),bo.getTextId());
    }
}
