package com.jzy.chatgptdata.domain.order.service.event.impl;

import com.jzy.chatgptdata.domain.order.service.event.IOrderEvent;
import com.jzy.chatgptdata.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class OrderPaySuccessEvent implements IOrderEvent {

    @Resource
    private IOrderService orderService;

    /**
     * 处理订单支付成功事件
     *
     * @param orderId       订单ID
     */
    public void handleOrderPaySuccess(String orderId) {
        try {
            log.info("开始处理订单支付成功逻辑，订单ID: {}", orderId);

            // 执行订单发货
            orderService.deliverGoods(orderId);

            log.info("订单支付成功处理完成，订单ID: {}", orderId);

        } catch (Exception e) {
            log.error("处理订单支付成功逻辑失败，订单ID: {}", orderId, e);
            // 可以根据需要添加重试或补偿逻辑
        }
    }

}
