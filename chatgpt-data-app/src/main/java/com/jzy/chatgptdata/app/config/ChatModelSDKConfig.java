package com.jzy.chatgptdata.app.config;

import com.jzy.chatglmsdk18753goldn.session.OpenAiSession;
import com.jzy.chatglmsdk18753goldn.session.OpenAiSessionFactory;
import com.jzy.chatglmsdk18753goldn.session.defaults.DefaultOpenAiSessionFactory;
import com.jzy.chatgptdata.types.sdk.token.BearerTokenUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description ChatGLMSDKConfig 工厂配置开启
 */
@Configuration
@EnableConfigurationProperties(ChatModelSDKConfigProperties.class)
public class ChatModelSDKConfig {

    @Bean(name = "glmOpenAiSession")
    @ConditionalOnProperty(value = "chat.sdk.config.enabled", havingValue = "true", matchIfMissing = false)
    public OpenAiSession createrGLMOpenAiSession(ChatModelSDKConfigProperties properties) {
        // 1. 配置文件
        com.jzy.chatglmsdk18753goldn.session.Configuration configuration = new com.jzy.chatglmsdk18753goldn.session.Configuration();
        configuration.setApiHost(properties.getApiHostGLM());
        configuration.setApiSecretKey(properties.getApiSecretKeyGLM());
        configuration.setToken(BearerTokenUtils.getGLMToken(properties.getApiSecretKeyGLM()));

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        return factory.openSession();
    }

    @Bean(name = "qwenOpenAiSession")
    @ConditionalOnProperty(value = "chat.sdk.config.enabled", havingValue = "true", matchIfMissing = false)
    public OpenAiSession createrQwenOpenAiSession(ChatModelSDKConfigProperties properties) {
        // 1. 配置文件
        com.jzy.chatglmsdk18753goldn.session.Configuration configuration = new com.jzy.chatglmsdk18753goldn.session.Configuration();
        configuration.setApiHost(properties.getApiHostQwen());
        configuration.setApiSecretKey(properties.getApiSecretKeyQwen());
        configuration.setToken(BearerTokenUtils.getQwenToken(properties.getApiSecretKeyQwen()));

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        return factory.openSession();
    }

}
