package com.jzy.chatgptdata.domain.openai.service.factory.impl;

import com.jzy.chatglmsdk18753goldn.model.ChatCompletionCommonRequest;
import com.jzy.chatglmsdk18753goldn.model.GLM.GLMChatCompletionRequest;
import com.jzy.chatgptdata.domain.openai.service.factory.ChatCompletionRequestFactory;
import org.springframework.stereotype.Component;

@Component
public class GLMChatCompletionRequestFactory implements ChatCompletionRequestFactory {
    @Override
    public ChatCompletionCommonRequest createRequest() {
        return new GLMChatCompletionRequest();
    }
}
