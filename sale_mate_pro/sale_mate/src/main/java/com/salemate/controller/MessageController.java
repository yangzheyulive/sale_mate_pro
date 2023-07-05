package com.salemate.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.salemate.bo.MessageBo;
import com.salemate.bo.MessageUpdateBo;
import com.salemate.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @PostMapping("/send")
    public ResponseEntity createMessageTask(@RequestBody MessageBo bo) throws IOException {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        bo.setCorpId(corpId);
        messageService.addMessageTask(bo.getCorpId(),bo.getSendTime(),bo.getSendType(),bo.getLabelType());
        return ResponseEntity.ok("ok");
    }


    @GetMapping("/getList")
    public ResponseEntity getList(String externalUserid) throws IOException {
        String corpId = StpUtil.getTokenSession().getString("corpId");
        return ResponseEntity.ok(messageService.getList(corpId,externalUserid));
    }
    /**
     * 发送朋友圈
     */
    @PostMapping("/sendFriend")
    public ResponseEntity sendFriend(@RequestBody MessageBo bo) throws IOException {
        messageService.sendFriend(bo.getMessageId());
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/updateMessage")
    public ResponseEntity updateMessage(@RequestBody MessageUpdateBo message) throws IOException {
        messageService.updateMessage(message);
        return ResponseEntity.ok("ok");
    }


}
