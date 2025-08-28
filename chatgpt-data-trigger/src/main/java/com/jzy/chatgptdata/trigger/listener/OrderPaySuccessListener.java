package com.jzy.chatgptdata.trigger.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.jzy.chatgptdata.domain.order.service.event.IOrderEvent;
import com.jzy.chatgptdata.domain.order.service.IOrderService;
import com.jzy.chatgptdata.infrastructure.kafka.dto.KafkaOrderPaySuccessMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @KafkaListener(
            topics = "${kafka.topics.order-pay-success:order-pay-success}",
            groupId = "${kafka.consumer-group.order-service:order-service-group}"
    )
    public void onOrderPaySuccess(ConsumerRecord<String, String> record) {
        try {
            log.info("Received order payment success message: {}", record.value());

            // 使用基础设施层的DTO进行反序列化
            KafkaOrderPaySuccessMessage message = objectMapper.readValue(
                    record.value(),
                    KafkaOrderPaySuccessMessage.class
            );

            orderOrderSuccessEvent.handleOrderPaySuccess(message.getOrderId());

            log.info("Successfully processed order payment: {}", message.getOrderId());

        } catch (Exception e) {
            log.error("Failed to process order payment success message: {}", record.value(), e);
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
