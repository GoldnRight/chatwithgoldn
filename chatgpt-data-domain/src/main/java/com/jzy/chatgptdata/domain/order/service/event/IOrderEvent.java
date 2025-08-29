package com.jzy.chatgptdata.domain.order.service.event;

import org.springframework.stereotype.Component;

@Component
public interface IOrderEvent {

    void handleOrderPaySuccess(String orderId);

    /**
     * 订单幂等性检测
     * @param orderId
     * @return
     */
    boolean isAlreadyProcessed(String orderId);

    /**
     * 标记订单已处理
     * @param orderId
     */
    void markAsProcessed(String orderId);
}
