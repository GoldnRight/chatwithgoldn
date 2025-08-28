package com.jzy.chatgptdata.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Kafka订单支付成功消息DTO
 * 位于基础设施层，包含Kafka消息的特定字段
 */
public class KafkaOrderPaySuccessMessage {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("partition_key")
    private String partitionKey; // Kafka分区键（技术特定）

    // 默认构造函数（Jackson反序列化需要）
    public KafkaOrderPaySuccessMessage() {}

    // 全参构造函数
    public KafkaOrderPaySuccessMessage(String orderId, String transactionId,
                                       BigDecimal amount, Date payTime) {
        this.orderId = orderId;
    }

    // Getter和Setter方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }
}