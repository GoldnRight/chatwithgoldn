package com.jzy.chatgptdata.domain.service.rule.impl;

import com.jzy.chatgptdata.domain.annotation.LogicStrategy;
import com.jzy.chatgptdata.domain.model.aggregates.ChatProcessAggregate;
import com.jzy.chatgptdata.domain.model.entity.MessageEntity;
import com.jzy.chatgptdata.domain.model.entity.RuleLogicEntity;
import com.jzy.chatgptdata.domain.model.entity.UserAccountQuotaEntity;
import com.jzy.chatgptdata.domain.model.valobj.LogicCheckTypeVO;
import com.jzy.chatgptdata.domain.service.rule.ILogicFilter;
import com.jzy.chatgptdata.domain.service.rule.factory.DefaultLogicFactory;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 敏感词过滤
 * @create 2023-09-16 17:39
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.SENSITIVE_WORD)
public class SensitiveWordFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Resource
    private SensitiveWordBs words;

    @Value("${app.config.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 白名单用户不做敏感词处理
        if (chatProcess.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        ChatProcessAggregate newChatProcessAggregate = new ChatProcessAggregate();
        newChatProcessAggregate.setOpenid(chatProcess.getOpenid());
        newChatProcessAggregate.setModel(chatProcess.getModel());

        List<MessageEntity> newMessages = chatProcess.getMessages().stream()
                .map(message -> {
                    String content = message.getContent();
                    String replace = words.replace(content);
                    return MessageEntity.builder()
                            .role(message.getRole())
                            .name(message.getName())
                            .content(replace)
                            .build();
                })
                .collect(Collectors.toList());

        newChatProcessAggregate.setMessages(newMessages);

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS)
                .data(newChatProcessAggregate)
                .build();
    }

}
