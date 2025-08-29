package com.jzy.chatgptdata.trigger.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.jzy.chatgptdata.domain.order.service.event.IOrderEvent;
import com.jzy.chatgptdata.domain.order.service.IOrderService;
import com.jzy.chatgptdata.infrastructure.kafka.dto.KafkaOrderPaySuccessMessage;
import com.jzy.chatgptdata.infrastructure.kafka.producer.DeadLetterProducer;
import com.jzy.chatgptdata.types.exception.NonRecoverableException;
import com.jzy.chatgptdata.types.exception.RecoverableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeoutException;

/**
 * 支付成功回调消息
 */
@Slf4j
@Component
public class OrderPaySuccessListener {

    @Resource
    private IOrderEvent orderOrderSuccessEvent;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private DeadLetterProducer deadLetterPublisher;

    @KafkaListener(
            topics = "${kafka.topics.order-pay-success:order-pay-success}",
            groupId = "${kafka.consumer-group.order-service:order-service-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderPaySuccess(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String message = record.value();
        log.info("Received order payment success message: {}", message);

        try {
            // 1. 反序列化消息
            KafkaOrderPaySuccessMessage payload = deserializeMessage(message);

            // 2. 幂等性检查
            if (orderOrderSuccessEvent.isAlreadyProcessed(payload.getOrderId())) {
                log.warn("Duplicate message detected for order: {}", payload.getOrderId());
                ack.acknowledge(); // 确认消息避免阻塞
                return;
            }

            // 3. 业务处理
            orderOrderSuccessEvent.handleOrderPaySuccess(payload.getOrderId());

            // 4. 标记处理成功
            orderOrderSuccessEvent.markAsProcessed(payload.getOrderId());

            log.info("Successfully processed order payment: {}", payload.getOrderId());

            // 5. 手动提交偏移量
            ack.acknowledge();

        } catch (RecoverableException e) {
            // 可恢复异常（如网络问题）
            log.warn("Recoverable error processing message: {}", message, e);
            // 不提交偏移量，等待重试

        } catch (NonRecoverableException e) {
            // 不可恢复异常（如数据格式错误）
            log.error("Non-recoverable error processing message: {}", message, e);
            deadLetterPublisher.publishToDlq(record, e);
            ack.acknowledge(); // 确认消息避免阻塞

        } catch (Exception  e) {
            // 未知异常处理
            log.error("Unexpected error processing message: {}", message, e);
            deadLetterPublisher.publishToDlq(record, e);
            ack.acknowledge(); // 确认消息避免阻塞
        }
    }

    private KafkaOrderPaySuccessMessage deserializeMessage(String message) throws JsonProcessingException  {
        try {
            return objectMapper.readValue(message, KafkaOrderPaySuccessMessage.class);
        } catch (JsonProcessingException e) {
            throw new NonRecoverableException ("Invalid message format", e);
        }
    }


//    @Resource
//    private IOrderService orderService;
//
//    @Subscribe
//    public void handleEvent(String orderId) {
//        try {
//            log.info("支付完成，发货并记录，开始。订单：{}", orderId);
//            orderService.deliverGoods(orderId);
//        } catch (Exception e) {
//            log.error("支付完成，发货并记录，失败。订单：{}", orderId, e);
//        }
//    }

}
