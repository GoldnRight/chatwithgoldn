package com.jzy.chatgptdata.app.config;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Resource
    private KafkaConfigProperties kafkaConfigProperties;

    public KafkaConfig(KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
    }

    /**
     * 生产者配置
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // 基本配置
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());

        // 从配置属性中获取生产者配置
        props.putAll(kafkaConfigProperties.getProducer().toMap());

        return props;
    }

    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka模板，用于发送消息
     */
    @Bean(name = "kafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 消费者配置
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // 基本配置
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigProperties.getConsumerGroupId());

        // 从配置属性中获取消费者配置
        props.putAll(kafkaConfigProperties.getConsumer().toMap());

        return props;
    }

    /**
     * 消费者工厂
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    /**
     * Kafka监听器容器工厂
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // 设置并发消费者数量
        factory.setConcurrency(kafkaConfigProperties.getListenerConcurrency());

        // 设置poll超时时间
        factory.getContainerProperties().setPollTimeout(kafkaConfigProperties.getListenerPollTimeout());

        return factory;
    }

    /**
     * Kafka管理客户端，用于创建主题等管理操作
     */
    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());

        // 从配置属性中获取管理配置
        if (kafkaConfigProperties.getAdmin() != null) {
            configs.putAll(kafkaConfigProperties.getAdmin());
        }

        return AdminClient.create(configs);
    }

    /**
     * JSON消息转换器
     */
    @Bean
    public StringJsonMessageConverter jsonMessageConverter() {
        return new StringJsonMessageConverter();
    }}
