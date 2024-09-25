package com.pxl.zkmall.order;

import com.pxl.zkmall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class zkmallOrderApplicationTest {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送java对象
     */
    @Test
    public void sendPojoMessage() {
        for (int i = 0; i < 10; i++) {
            OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
            orderReturnReasonEntity.setId(1l);
            orderReturnReasonEntity.setName("hahaha---"+i);
            orderReturnReasonEntity.setSort(1);
            orderReturnReasonEntity.setStatus(0);
            orderReturnReasonEntity.setCreateTime(new Date());
            rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity);
            log.info("发送对象消息成功");
        }

    }


    @Test
    public void sendMessageTest() {
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", "hello java");
        log.info("消息发送成功：{}","hello java");
    }


    /**
     * 1、如何创建exchange queue 、Bingding
     *   使用amqAdmin进行创建
     *  2、如何接收消息
     */
    @Test
    public void createExchange() {
        /**
         * DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
         */
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("交换机创建完成：{}","hello-java-exchange");
    }

    @Test
    public void createQueue() {
        /**
         *  Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,
         *  @Nullable Map<String, Object> arguments)
         *
         */
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("队列创建完成：{}","hello-java-queue");

    }

    @Test
    public void createBingding() {
        /**
         * (String destination,  [目的地]
         * DestinationType destinationType, 【目的地类型】
         * String exchange,【交换机】
         * String routingKey,【路由键】
         * @Nullable Map<String, Object> arguments)  【自定义参数】
         *
         */
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                                "hello.java",
                null);
        amqpAdmin.declareBinding(binding);
        log.info("bingding创建完成：{}","bingding");
    }
}
