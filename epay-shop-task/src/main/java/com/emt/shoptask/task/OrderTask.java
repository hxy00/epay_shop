package com.emt.shoptask.task;

import com.emt.shoptask.sv.inter.IPayPostSv;
import com.emt.shoptask.sv.inter.IPaySv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Mr.Huang
 * 订单任务（未支付订单处理、已支付订单处理）
 */
@Component
@EnableScheduling
public class OrderTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IPaySv iPaySv;

    @Autowired
    private IPayPostSv iPayPostSv;

    @Scheduled(cron = "0 */5 * * * ?") //
    public void alreadyPayPostTask() {
        logger.info("[alreadyPayPostTask]定时任务开始执行");
        alreadyPayPost();
        logger.info("[alreadyPayPostTask]定时任务开始结束");
    }

    @Scheduled(cron = "0 */5 * * * ?") //
    public void unPayQueryTask() {
        logger.info("[unPayQueryTask]定时任务开始执行");
        unPayQuery();
        logger.info("[unPayQueryTask]5分钟执行一次结束");
    }

    /**
     * 处理：已支付，未推送
     */
    public void alreadyPayPost(){
        iPayPostSv.alreadyPayPost();
    }

    /**
     * 处理：未支付，进行查询并推送
     */
    public void unPayQuery(){
        int orderCloseTime = 6;
        iPaySv.unPayQuery(orderCloseTime, "400001");
    }

}
