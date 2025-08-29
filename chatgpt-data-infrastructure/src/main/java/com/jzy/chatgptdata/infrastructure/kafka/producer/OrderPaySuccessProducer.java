package com.jzy.chatgptdata.infrastructure.kafka.producer;

import com.jzy.chatgptdata.domain.order.producer.IOrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderPaySuccessProducer implements IOrderProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.order-dead-letter}")
    private String kafkaDeadLetterTopic;

    @Value("${kafka.topics.order-pay-success}")
    private String kafkaTopic;

    @Value("${kafka.producer.max-retries:5}")
    private Integer maxRetries;

    public void sendPaymentSuccessMessage(String tradeNo) {
        int attempt = 0;
        while (attempt <= maxRetries) {
            try {
                String message = createPaymentMessage(tradeNo);
                // 同步发送 + 超时控制
                SendResult<String, String> result = kafkaTemplate.send(kafkaTopic, tradeNo, message)
                        .get(100, TimeUnit.SECONDS); // 同步等待10s

                log.info("支付消息持久化 | 订单:{} P:{} O:{}",
                        tradeNo,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                return;

            } catch (TimeoutException te) {
                attempt++;
                log.warn("支付消息发送超时 [{}] 重试 {}/{}", tradeNo, attempt, maxRetries);

            } catch (InterruptedException | ExecutionException e) {
                // 立即失败的错误类型
                log.error("不可恢复发送失败 [{}] 原因: {}", tradeNo, e.getMessage());
                return;
            } catch (java.util.concurrent.TimeoutException e) {
                throw new RuntimeException(e);
            }

            // 指数退避重试策略
            try {
                long backoff = (long) (Math.pow(2, attempt) * 100 + ThreadLocalRandom.current().nextInt(100));
                Thread.sleep(Math.min(backoff, 5000));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        // 重试耗尽处理
        log.error("支付消息发送失败 [{}] 已达最大重试次数", tradeNo);
        saveToDeadLetterQueue(tradeNo, "Kafka发送失败");
    }

    /**
     * 创建支付消息内容
     */
    private String createPaymentMessage(String tradeNo) {
        // 简单方式：只发送订单号（适用于简单场景）
        // return tradeNo;

        // 推荐方式：发送结构化数据（JSON格式）
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("orderId", tradeNo);

        // 转换为JSON字符串（这里简化处理，实际应用中可以使用Jackson）
        return "{" + "\"orderId\":\"" + tradeNo + "\"" + "}";
    }

    // 死信存储实现
    private void saveToDeadLetterQueue(String tradeNo, String reason) {
        String message = createPaymentMessage(tradeNo);
        toDeadLetter(kafkaDeadLetterTopic, tradeNo, message);
        log.warn("➡️ 消息转入死信队列: {}", tradeNo);
    }

    private void toDeadLetter(String kafkaDlq, String tradeNo, String message) {
        Integer attempt = 0;
        while(attempt <= maxRetries){
            try {
                // 同步发送 + 超时控制
                SendResult<String, String> result = kafkaTemplate.send(kafkaDlq, tradeNo, message)
                        .get(10, TimeUnit.SECONDS); // 同步等待10s

                log.info("死信队列消息持久化 | 订单:{} P:{} O:{}",
                        tradeNo,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } catch (TimeoutException te) {
                attempt++;
                log.warn("消息发送到死信队列超时 [{}] 重试 {}/{}", tradeNo, attempt, maxRetries);
            } catch (InterruptedException | ExecutionException e) {
                // 立即失败的错误类型
                log.error("消息发送到死信队列，不可恢复发送失败 [{}] 原因: {}", tradeNo, e.getMessage());
                return;
            } catch (java.util.concurrent.TimeoutException e) {
                throw new RuntimeException(e);
            }
            // 指数退避重试策略
            try {
                long backoff = (long) (Math.pow(2, attempt) * 100 + ThreadLocalRandom.current().nextInt(100));
                Thread.sleep(Math.min(backoff, 5000));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }


}
