
package com.jzy.chatgptdata.test;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class KafkaTestConsumer {

    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "${kafka.default-topic:opsxlab_test}", groupId = "test-consumer-group")
    public void listen(String message) {
        log.info("测试消费者接收到消息: {}", message);
        // 减少计数器，表示消息已接收
        latch.countDown();
    }
}