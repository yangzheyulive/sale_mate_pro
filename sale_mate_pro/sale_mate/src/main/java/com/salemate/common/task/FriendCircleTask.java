package com.salemate.common.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.salemate.model.Message;
import com.salemate.service.EnterpriseService;
import com.salemate.service.MessageService;
import com.salemate.common.util.WxConstantKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 朋友圈任务
 */
@Component("messageTask")
@Slf4j
public class FriendCircleTask implements ITask {
    @Autowired
    private MessageService messageService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Override
    public void run(String messageId){
        //只发朋友圈
        List<Message> list = messageService.list(new QueryWrapper<Message>().eq("status", 0)
                .eq("send_type", WxConstantKeys.SEND_TYPE.CIRCLE.getValue()).lt("send_date", new Date()));
        List<Message> msgList = messageService.list(new QueryWrapper<Message>().eq("status", 0)
                .eq("send_type", WxConstantKeys.SEND_TYPE.MSG.getValue()).lt("send_date", new Date()));

        log.info("TestTask定时任务正在执行，参数为：{}", list);
        for (Message message : list) {
            try {
                messageService.sendFriend(message.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        for (Message message : msgList) {
            try {
                messageService.sendMsg(message.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
