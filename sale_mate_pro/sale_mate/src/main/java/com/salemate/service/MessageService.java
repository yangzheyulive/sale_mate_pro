package com.salemate.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.salemate.bo.MessageUpdateBo;
import com.salemate.model.Message;

import java.io.IOException;
import java.util.List;

public interface MessageService extends IService<Message> {
    void sendMsg(String messageId) throws IOException;

    void sendFriend(String messageId) throws IOException;

    // 可以添加自定义的业务方法
    public void addMessageTask(String corpId, String sendTime,Integer sendType,Integer labelType) throws IOException;

    List<JSONObject> getList(String corpId , String externalUserid);

    void updateMessage(MessageUpdateBo message);

}
