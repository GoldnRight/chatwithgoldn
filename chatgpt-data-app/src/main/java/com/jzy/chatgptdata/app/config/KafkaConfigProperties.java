package com.jzy.chatgptdata.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigProperties {
    // 基本配置
    private String bootstrapServers = "120.26.196.227:9094";
    private String defaultTopic = "opsxlab_test";
    private String consumerGroupId = "chatgpt-data-group";
    private int retries = 3;

    // 生产者配置
    private Producer producer = new Producer();

    // 消费者配置
    private Consumer consumer = new Consumer();

    // 管理配置
    private Map<String, Object> admin = new HashMap<>();

    // 监听器配置
    private Integer listenerConcurrency = 3;
    private Long listenerPollTimeout = 3000L;

    @Data
    public static class Producer {
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String acks = "all";
        private Boolean enableIdempotence = true;
        private Integer maxInFlightRequestsPerConnection = 1;
        private Integer retries = 10;
        private Integer deliveryTimeoutMs = 120000;
        private Integer requestTimeoutMs = 30000;
        private Integer lingerMs = 20;
        private String compressionType = "gzip";
        private Integer batchSize = 32768;
        private Long bufferMemory = 33554432L;

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("key.serializer", this.keySerializer);
            map.put("value.serializer", this.valueSerializer);
            map.put("acks", this.acks);
            map.put("enable.idempotence", this.enableIdempotence);
            map.put("max.in.flight.requests.per.connection", this.maxInFlightRequestsPerConnection);
            map.put("retries", this.retries);
            map.put("delivery.timeout.ms", this.deliveryTimeoutMs);
            map.put("request.timeout.ms", this.requestTimeoutMs);
            map.put("linger.ms", this.lingerMs);
            map.put("compression.type", this.compressionType);
            map.put("batch.size", this.batchSize);
            map.put("buffer.memory", this.bufferMemory);
            return map;
        }    }

    @Data
    public static class Consumer {
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String autoOffsetReset = "latest";
        private Boolean enableAutoCommit = false;
        private Integer maxPollRecords = 50;
        private Integer sessionTimeoutMs = 10000;

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("key.deserializer", this.keyDeserializer);
            map.put("value.deserializer", this.valueDeserializer);
            map.put("auto.offset.reset", this.autoOffsetReset);
            map.put("enable.auto.commit", this.enableAutoCommit);
            map.put("max.poll.records", this.maxPollRecords);
            map.put("max.poll.records", this.maxPollRecords);
            map.put("session.timeout.ms", this.sessionTimeoutMs);
            return map;
        }
    }}
