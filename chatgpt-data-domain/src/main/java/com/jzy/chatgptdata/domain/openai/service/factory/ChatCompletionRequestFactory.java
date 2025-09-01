package com.jzy.chatgptdata.domain.openai.service.factory;

import com.jzy.chatglmsdk18753goldn.model.ChatCompletionCommonRequest;

public interface ChatCompletionRequestFactory {
    ChatCompletionCommonRequest createRequest();
}