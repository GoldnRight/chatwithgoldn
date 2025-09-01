package com.jzy.chatgptdata.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description ChatGLMSDKConfigProperties
 */
@Data
@ConfigurationProperties(prefix = "chat.sdk.config", ignoreInvalidFields = true)
public class ChatModelSDKConfigProperties {

    /**
     * 状态；open = 开启、close 关闭
     */
    private boolean enable;
    /**
     * 转发地址
     */
    private String apiHostGLM;
    /**
     * 可以申请 sk-***
     */
    private String apiSecretKeyGLM;

    private String apiHostQwen;

    private String apiSecretKeyQwen;

}
