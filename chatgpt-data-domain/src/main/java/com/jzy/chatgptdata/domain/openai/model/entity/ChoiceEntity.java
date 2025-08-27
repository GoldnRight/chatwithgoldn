package com.jzy.chatgptdata.domain.openai.model.entity;

import lombok.Data;

/**
 * @description 聊天响应选项
 */
@Data
public class ChoiceEntity {

    /** stream = true 请求参数里返回的属性是 delta */
    private MessageEntity delta;
    /** stream = false 请求参数里返回的属性是 delta */
    private MessageEntity message;

}
