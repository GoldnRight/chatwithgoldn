package com.jzy.chatgptdata.infrastructure.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jzy.chatgptdata.infrastructure.kafka.dto.DeadLetterMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class DeadLetterProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private ObjectMapper objectMapper;

    public void publishToDlq(ConsumerRecord<?, ?> originalRecord, Exception exception) {
        try {
            // 构建死信消息
            DeadLetterMessage dlqMessage = new DeadLetterMessage(
                    originalRecord.value().toString(),
                    exception.getMessage(),
                    System.currentTimeMillis(),
                    originalRecord.topic(),
                    originalRecord.partition(),
                    originalRecord.offset()
            );

            // 转换为JSON
            String dlqPayload = objectMapper.writeValueAsString(dlqMessage);

            // 发送到死信队列
            kafkaTemplate.send(
                    originalRecord.topic() + ".DLQ",
                    originalRecord.key() != null ? originalRecord.key().toString() : "null",
                    dlqPayload
            ).addCallback(
                    result -> log.info("Message sent to DLQ: {}", dlqMessage),
                    ex -> log.error("Failed to send to DLQ: {}", dlqMessage, ex)
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize DLQ message", e);
        }
    }
}
