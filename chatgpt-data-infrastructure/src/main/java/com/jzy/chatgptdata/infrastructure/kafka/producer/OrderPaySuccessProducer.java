package com.jzy.chatgptdata.infrastructure.kafka.producer;

import com.jzy.chatgptdata.domain.order.producer.IOrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OrderPaySuccessProducer implements IOrderProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendPaymentSuccessMessage(String tradeNo) {
        try {
            // 创建消息内容
            String message = createPaymentMessage(tradeNo);

            // 发送消息到 Kafka
            kafkaTemplate.send("order-pay-success", tradeNo, message)
                    .addCallback(
                            result -> {
                                if (result != null) {
                                    log.info("支付成功消息已发送到Kafka, topic: {}, partition: {}, offset: {}",
                                            result.getRecordMetadata().topic(),
                                            result.getRecordMetadata().partition(),
                                            result.getRecordMetadata().offset());
                                }
                            },
                            ex -> log.error("发送支付成功消息到Kafka失败: {}", ex.getMessage())
                    );

            log.info("已发送支付成功消息到Kafka, 订单号: {}", tradeNo);
        } catch (Exception e) {
            log.error("发送支付成功消息到Kafka异常", e);
        }
    }

    /**
     * 创建支付消息内容
     */
    private String createPaymentMessage(String tradeNo) {
        // 简单方式：只发送订单号（适用于简单场景）
        // return tradeNo;

        // 推荐方式：发送结构化数据（JSON格式）
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("orderNo", tradeNo);

        // 转换为JSON字符串（这里简化处理，实际应用中可以使用Jackson）
        return "{" +
                "\"orderNo\":\"" + tradeNo + "\"," ;
    }

}
