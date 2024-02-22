package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
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

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void sendMessageTest() {
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setName("测试");
        for (int i = 0; i < 10; i++) {
            reasonEntity.setName("测试" + i);
            rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
        }
//        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
//        String msg = "I love ZangYijia!";
//        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", msg);
        log.info("消息发送成功：{}", reasonEntity);
    }
    @Test
    public void createExchange() {
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}] created successfully", directExchange.getName());
    }
    @Test
    public void createQueue() {
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}] created successfully", queue.getName());
    }

    @Test
    public void createBinding() {
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}] created successfully", binding.getDestination());
    }

}
