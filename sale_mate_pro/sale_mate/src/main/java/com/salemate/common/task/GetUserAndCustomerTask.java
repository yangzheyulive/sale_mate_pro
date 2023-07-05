package com.salemate.common.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Slf4j
@Component
public class GetUserAndCustomerTask {


    // 上次任务执行完毕的时间点之后1小时再执行
    @Scheduled(fixedDelay = 60*60*1000)
//    @Scheduled(fixedDelay = 1000)
    public void schedule() throws ParseException {

    }
}
