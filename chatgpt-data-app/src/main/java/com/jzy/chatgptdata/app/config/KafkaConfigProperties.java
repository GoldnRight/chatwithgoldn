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
        private String acks = "1";
        private Integer retries = 3;
        private Integer batchSize = 16384;
        private Integer lingerMs = 5;
        private Long bufferMemory = 33554432L;

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("key.serializer", this.keySerializer);
            map.put("value.serializer", this.valueSerializer);
            map.put("acks", this.acks);
            map.put("retries", this.retries);
            map.put("batch.size", this.batchSize);
            map.put("linger.ms", this.lingerMs);
            map.put("buffer.memory", this.bufferMemory);
            return map;
        }
    }

    @Data
    public static class Consumer {
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String autoOffsetReset = "earliest";
        private Boolean enableAutoCommit = true;
        private Integer autoCommitIntervalMs = 1000;
        private Integer maxPollRecords = 500;
        private Integer sessionTimeoutMs = 10000;

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("key.deserializer", this.keyDeserializer);
            map.put("value.deserializer", this.valueDeserializer);
            map.put("auto.offset.reset", this.autoOffsetReset);
            map.put("enable.auto.commit", this.enableAutoCommit);
            map.put("auto.commit.interval.ms", this.autoCommitIntervalMs);
            map.put("max.poll.records", this.maxPollRecords);
            map.put("session.timeout.ms", this.sessionTimeoutMs);
            return map;
        }
    }}
