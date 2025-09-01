package com.jzy.chatgptdata.domain.openai.service.factory.impl;

import com.jzy.chatglmsdk18753goldn.model.ChatCompletionCommonRequest;
import com.jzy.chatglmsdk18753goldn.model.Qwen.QwenChatCompletionRequest;
import com.jzy.chatgptdata.domain.openai.service.factory.ChatCompletionRequestFactory;
import org.springframework.stereotype.Component;

@Component
public class QwenChatCompletionRequestFactory implements ChatCompletionRequestFactory {
    @Override
    public ChatCompletionCommonRequest createRequest() {
        return new QwenChatCompletionRequest();
    }
}
