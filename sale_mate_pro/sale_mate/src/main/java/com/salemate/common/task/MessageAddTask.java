package com.salemate.common.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.salemate.model.Enterprise;
import com.salemate.model.Message;
import com.salemate.service.EnterpriseService;
import com.salemate.service.MessageService;
import com.salemate.common.util.WxApiUtil;
import com.salemate.common.util.WxConstantKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component("messageAddTask")
@Slf4j
public class MessageAddTask implements ITask {
    @Autowired
    private MessageService messageService;
    @Autowired
    private EnterpriseService enterpriseService;

    @Override
    public void run(String params) {
        log.info("MessageTask定时任务正在执行，参数为：{}", params);
        //发布朋友圈消息
        List<Enterprise> list = enterpriseService.list(new QueryWrapper<Enterprise>()
                .eq("suite_id", WxApiUtil.getWxProperties().getSuiteId()));

        for (Enterprise enterprise : list) {
            //如果第二天没有要发送的消息则添加消息
            DateTime dateTime1 = DateUtil.offsetDay(new Date(), 1);
            //查询第二天是否有要发送的消息
            Long count = messageService.count(new QueryWrapper<Message>().eq("corp_id", enterprise.getCorpId())
                    .gt("send_time", DateUtil.format(dateTime1, "yyyy-MM-dd") + " 01:00:00")
                    .eq("send_type", WxConstantKeys.SEND_TYPE.CIRCLE.getValue()));
            if (count  != 0) {
                Long towWeek = getTowWeek();
                for (int i = 1; i < towWeek + 1; i++) {
                    DateTime dateTime = DateUtil.offsetDay(new Date(), i);
                    String sendTime = DateUtil.format(dateTime, "yyyy-MM-dd");
                    if (StrUtil.isBlank(enterprise.getUserSecret()) || StrUtil.isBlank(enterprise.getCustomerSecret())) {
                        log.info("企业信息不完整，corpId为：{}", enterprise.getCorpId());
                        continue;
                    }
                    try {
                        //添加朋友圈消息
                        String sendTimeZ = sendTime + " 11:00:00";
                        messageService.addMessageTask(enterprise.getCorpId(), sendTimeZ, WxConstantKeys.SEND_TYPE.CIRCLE.getValue(), WxConstantKeys.LabelType.Buy.getValue());
                        messageService.addMessageTask(enterprise.getCorpId(), sendTimeZ, WxConstantKeys.SEND_TYPE.CIRCLE.getValue(), WxConstantKeys.LabelType.NO_Buy.getValue());
                        messageService.addMessageTask(enterprise.getCorpId(), sendTimeZ, WxConstantKeys.SEND_TYPE.CIRCLE.getValue(), WxConstantKeys.LabelType.INTRODUCE.getValue());

                        String sendTimeS = sendTime + " 20:30:00";
                        messageService.addMessageTask(enterprise.getCorpId(), sendTimeS, WxConstantKeys.SEND_TYPE.CIRCLE.getValue(), WxConstantKeys.LabelType.Buy.getValue());
                        messageService.addMessageTask(enterprise.getCorpId(), sendTimeS, WxConstantKeys.SEND_TYPE.CIRCLE.getValue(), WxConstantKeys.LabelType.NO_Buy.getValue());
                        messageService.addMessageTask(enterprise.getCorpId(), sendTimeS, WxConstantKeys.SEND_TYPE.CIRCLE.getValue(), WxConstantKeys.LabelType.INTRODUCE.getValue());
                    } catch (IOException e) {
                        log.info("执行失败，messageId为：{}", params);
                    }

                }
            }
        }
    }

    public static Long getTowWeek() {
        Date now = new Date();
        int day = DateUtil.dayOfWeek(now);
        int offset = 7 - day + 1;
        DateTime dateTime = DateUtil.offsetWeek(now, 1);
        DateTime end = DateUtil.offsetDay(dateTime, offset);
        return DateUtil.betweenDay(now, end, false);
    }
}
