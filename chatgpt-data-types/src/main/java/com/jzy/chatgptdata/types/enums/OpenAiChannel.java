package com.jzy.chatgptdata.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description OpenAi 渠道
 */
@Getter
@AllArgsConstructor
public enum OpenAiChannel {

    ChatGLM("ChatGLM"),
    ChatGPT("ChatGPT"),
    Qwen("Qwen"),

    ;
    private final String code;

    public static OpenAiChannel getChannel(String model) {
        if (model.toLowerCase().contains("gpt")) return OpenAiChannel.ChatGPT;
        if (model.toLowerCase().contains("glm")) return OpenAiChannel.ChatGLM;
        if (model.toLowerCase().contains("cogview")) return OpenAiChannel.ChatGLM;
        if (model.toLowerCase().contains("qwen")) return OpenAiChannel.Qwen;
        return null;
    }

}
