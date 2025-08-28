package com.jzy.chatgptdata.test;

import com.jzy.chatgptdata.app.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class KafkaTest {

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @Resource
    private KafkaTestConsumer kafkaTestConsumer;

    @Test
    public void testKafkaProducerAndListener() throws InterruptedException {
        // 发送消息
        String topic = "opsxlab_test";
        String key = "test-key";
        String message = "Hello Kafka Integration Test!";

        log.info("发送消息到主题 {}: Key={}, Message={}", topic, key, message);

        // 使用配置好的KafkaTemplate发送消息
        kafkaTemplate.send(topic, key, message);

        // 等待监听器处理消息
        if (kafkaTestConsumer.getLatch().await(15, TimeUnit.SECONDS)) {
            log.info("成功接收到并处理了Kafka消息");
        } else {
            log.warn("在超时时间内未接收到处理完成的通知");
        }
    }
}
