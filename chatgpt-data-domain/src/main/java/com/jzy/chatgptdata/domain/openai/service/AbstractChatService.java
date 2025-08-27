package com.jzy.chatgptdata.domain.openai.service;

import com.jzy.chatgptdata.domain.openai.model.aggregates.ChatProcessAggregate;
import com.jzy.chatgptdata.domain.openai.model.entity.RuleLogicEntity;
import com.jzy.chatgptdata.domain.openai.model.entity.UserAccountQuotaEntity;
import com.jzy.chatgptdata.domain.openai.model.valobj.LogicCheckTypeVO;
import com.jzy.chatgptdata.domain.openai.repository.IOpenAiRepository;
import com.jzy.chatgptdata.domain.openai.service.channel.OpenAiGroupService;
import com.jzy.chatgptdata.domain.openai.service.channel.impl.ChatGLMService;
import com.jzy.chatgptdata.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.jzy.chatgptdata.types.common.Constants;
import com.jzy.chatgptdata.types.enums.OpenAiChannel;
import com.jzy.chatgptdata.types.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 对话模型抽象类
 */
@Slf4j
public abstract class AbstractChatService implements IChatService {

    private final Map<OpenAiChannel, OpenAiGroupService> openAiGroup = new HashMap<>();

    public AbstractChatService(ChatGLMService chatGLMService) {
        openAiGroup.put(OpenAiChannel.ChatGLM, chatGLMService);
    }

//    @Resource
//    private Cache<String, String> codeCache;

    @Resource
    private IOpenAiRepository openAiRepository;

    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        try {
            // 1. 请求应答
            emitter.onCompletion(() -> {
                log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
            });
            emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

            // 2. 账户获取
            UserAccountQuotaEntity userAccountQuotaEntity = openAiRepository.queryUserAccount(chatProcess.getOpenid());
//            UserAccountQuotaEntity userAccountQuotaEntity = new UserAccountQuotaEntity();
//            userAccountQuotaEntity.setOpenid(chatProcess.getOpenid());
            // 3. 规则过滤
//            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess,userAccountQuotaEntity,
//                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
//                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode());
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess, userAccountQuotaEntity,
                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.ACCOUNT_STATUS.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.MODEL_TYPE.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.USER_QUOTA.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode()
            );
//
            if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 4. 应答处理 【ChatGPT、ChatGLM 策略模式】
//            openAiGroup.get(chatProcess.getChannel()).doMessageResponse(chatProcess, emitter);
            openAiGroup.get(chatProcess.getChannel()).doMessageResponse(ruleLogicEntity.getData(), emitter);
        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 5. 返回结果
        return emitter;
    }

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, String... logics) throws Exception;


    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception;

}
