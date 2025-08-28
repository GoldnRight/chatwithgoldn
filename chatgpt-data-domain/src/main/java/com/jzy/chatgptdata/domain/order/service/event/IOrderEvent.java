package com.jzy.chatgptdata.domain.order.service.event;

import org.springframework.stereotype.Component;

@Component
public interface IOrderEvent {

    void handleOrderPaySuccess(String orderId);
}
