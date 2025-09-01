package com.jzy.chatgptdata.domain.openai.service.factory;

import com.jzy.chatgptdata.domain.openai.service.factory.impl.QwenChatCompletionRequestFactory;
import com.jzy.chatgptdata.domain.openai.service.factory.impl.GLMChatCompletionRequestFactory;
import com.jzy.chatgptdata.types.enums.ChatModel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatCompletionRequestFactoryMapper {
    @Resource
    private GLMChatCompletionRequestFactory GLMChatCompletionRequestFactory;
    @Resource
    private QwenChatCompletionRequestFactory qwenChatCompletionRequestFactory;

    private final Map<String, ChatCompletionRequestFactory> factoryMap = new ConcurrentHashMap<>();
    @PostConstruct
    public void init() {
        // 注册模型和策略映射关系
        registerMapping(ChatModel.COGVIEW_3.getCode(), GLMChatCompletionRequestFactory);
        registerMapping(ChatModel.GLM_45_FLASH.getCode(), GLMChatCompletionRequestFactory);
        registerMapping(ChatModel.GLM_Z1_AIR.getCode(), GLMChatCompletionRequestFactory);
        registerMapping(ChatModel.GLM_Z1_FLASH.getCode(), GLMChatCompletionRequestFactory);
//        registerMapping(ChatModel.DeepSeek_V3.getCode(), qwenChatCompletionRequestFactory);
        registerMapping(ChatModel.Qwen3_Coder_Plus.getCode(), qwenChatCompletionRequestFactory);
    }

    private void registerMapping(String modelName, ChatCompletionRequestFactory factory) {
        factoryMap.put(modelName, factory);
    }

    public ChatCompletionRequestFactory getFactory(String modelName) {
        ChatCompletionRequestFactory factory = factoryMap.get(ChatModel.get(modelName).getCode());
        if (factory == null) {
            throw new IllegalArgumentException(modelName + "未注册，暂时无法使用");
        }
        return factory;
    }
}
