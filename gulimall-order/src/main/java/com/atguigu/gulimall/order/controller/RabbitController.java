package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RabbitController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq() {
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setName("测试");
        for (int i = 0; i < 10; i++) {
            reasonEntity.setName("测试" + i);
            rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity, new CorrelationData(UUID.randomUUID().toString()));
        }
        return "success";
    }
}
