package com.jzy.chatgptdata.domain.openai.model.aggregates;

import com.jzy.chatgptdata.types.enums.ChatModel;
import com.jzy.chatgptdata.domain.openai.model.entity.MessageEntity;
import com.jzy.chatgptdata.types.common.Constants;
import com.jzy.chatgptdata.types.enums.OpenAiChannel;
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
public class ChatProcessAggregate {

    /** 用户ID */
    private String openid;
    /** 默认模型 */
    private String model = ChatModel.GLM_45_FLASH.getCode();
    /** 问题描述 */
    private List<MessageEntity> messages;
    /** 会话ID */
    private String sessionId;

    public boolean isWhiteList(String whiteListStr) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(openid)) return true;
        }
        return false;
    }

    public OpenAiChannel getChannel(){
        return OpenAiChannel.getChannel(this.model);
    }

}
