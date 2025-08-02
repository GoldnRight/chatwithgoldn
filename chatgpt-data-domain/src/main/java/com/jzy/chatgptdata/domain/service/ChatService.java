package com.jzy.chatgptdata.domain.service;

import com.jzy.chatgptdata.domain.model.aggregates.ChatProcessAggregate;
import com.jzy.chatgptdata.domain.model.entity.RuleLogicEntity;
import com.jzy.chatgptdata.domain.model.entity.UserAccountQuotaEntity;
import com.jzy.chatgptdata.domain.model.valobj.LogicCheckTypeVO;
import com.jzy.chatgptdata.domain.service.channel.impl.ChatGLMService;
//import com.jzy.chatgptdata.domain.service.channel.impl.ChatGPTService;
import com.jzy.chatgptdata.domain.service.rule.ILogicFilter;
import com.jzy.chatgptdata.domain.service.rule.factory.DefaultLogicFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2023-07-22 21:11
 */
@Service
public class ChatService extends AbstractChatService {

    @Resource
    private DefaultLogicFactory logicFactory;

    public ChatService(ChatGLMService chatGLMService) {
        super(chatGLMService);
    }

    @Override
    protected RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception {
        Map<String, ILogicFilter<UserAccountQuotaEntity>> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatProcessAggregate> entity = null;
        for (String code : logics) {
            if (DefaultLogicFactory.LogicModel.NULL.getCode().equals(code)) continue;
            entity = logicFilterMap.get(code).filter(chatProcess, userAccountQuotaEntity);
            if (!LogicCheckTypeVO.SUCCESS.equals(entity.getType())) return entity;
        }
        return entity != null ? entity : RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
    }

}
