package com.pxl.zkmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }



    /**
     * 当MyRabbitConfig对象创建完成后，执行此方法
     */
    @PostConstruct
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData  消息的唯一id
             * @param b                broker是否接收到消息
             * @param s                失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("correlationData：" + correlationData + "===>broker是否接收到消息：" + b + "===>失败的原因：" + s);
            }
        });

        //设置消息队列抵达确认
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递到指定的队列，就触发这个失败回调
             * @param message 消息的详细内容
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange 发给哪个交换机
             * @param routingKey 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("Fail Message：" + message + "===>replyCode：" + replyCode + "===>replyText：" + replyText + "===>exchange：" + exchange + "===>routingKey：" + routingKey);
            }
        });
    }


}
