package com.pxl.zkmall.order.controller;


import com.pxl.common.utils.R;
import com.pxl.zkmall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;


@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMessage")
    public R sendMessage(@RequestParam(value = "num", defaultValue = "10") Integer num) {
        for (int i = 0; i < num; i++) {
            OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
            orderReturnReasonEntity.setId(1L);
            orderReturnReasonEntity.setName("哈哈--" + i);
            orderReturnReasonEntity.setSort(1);
            orderReturnReasonEntity.setStatus(0);
            orderReturnReasonEntity.setCreateTime(new Date());

            rabbitTemplate.convertAndSend("hello-java-exchange",
                    "hello.java",
                    orderReturnReasonEntity,
                    new CorrelationData(UUID.randomUUID().toString()));
            System.out.println("发送对象消息成功");
        }
        return R.ok();
    }
}
