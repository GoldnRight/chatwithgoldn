package com.jzy.chatgptdata.domain.openai.service.rule;

import com.jzy.chatgptdata.domain.openai.model.aggregates.ChatProcessAggregate;
import com.jzy.chatgptdata.domain.openai.model.entity.RuleLogicEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 规则过滤接口
 * @create 2023-09-16 16:59
 */
public interface ILogicFilter<T> {

    RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, T data) throws Exception;

}
