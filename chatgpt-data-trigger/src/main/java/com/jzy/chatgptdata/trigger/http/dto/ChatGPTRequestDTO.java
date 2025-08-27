package com.jzy.chatgptdata.trigger.http.dto;

import com.jzy.chatgptdata.types.enums.ChatGLMModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequestDTO {

    /** 默认模型 */
    private String model = ChatGLMModel.CHATGLM_STD.getCode();

    /** 问题描述 */
    private List<MessageEntity> messages;

}
