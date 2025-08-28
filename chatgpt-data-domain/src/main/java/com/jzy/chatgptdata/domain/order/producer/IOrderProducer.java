package com.jzy.chatgptdata.domain.order.producer;

import org.springframework.stereotype.Service;

@Service
public interface IOrderProducer {
    void sendPaymentSuccessMessage(String tradeNo);
}
