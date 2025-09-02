package com.jzy.chatgptdata.domain.openai.service.channel.impl;

import com.alibaba.fastjson.JSON;
import com.jzy.chatglmsdk18753goldn.model.ChatCompletionCommonRequest;
import com.jzy.chatglmsdk18753goldn.model.ChatCompletionResponse;
import com.jzy.chatglmsdk18753goldn.model.EventType;
import com.jzy.chatglmsdk18753goldn.model.Qwen.QwenChatCompletionRequest;
import com.jzy.chatglmsdk18753goldn.session.OpenAiSession;
import com.jzy.chatgptdata.domain.openai.model.aggregates.ChatProcessAggregate;
import com.jzy.chatgptdata.domain.openai.service.channel.OpenAiGroupService;
import com.jzy.chatgptdata.domain.openai.service.factory.ChatCompletionRequestFactoryMapper;
import com.jzy.chatgptdata.types.common.PrometheusCollectionConstants;
import com.jzy.chatgptdata.types.enums.ChatModel;
import com.jzy.chatgptdata.types.exception.ChatGPTException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description Qwen 服务
 */
@Slf4j
@Service
public class QwenService implements OpenAiGroupService {
    @Resource
    protected OpenAiSession qwenOpenAiSession;

    @Resource
    private MeterRegistry registry;

//    @Resource
//    private ChatCompletionRequestFactoryMapper chatCompletionRequestFactoryMapper;

    @Resource
    private ThreadPoolExecutor prometheusCollectionThreadPoolExecutor;

    // 首字延迟统计
    private final Map<String, Long> firstTokenTimeMap = new ConcurrentHashMap<>();


    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {
        // 1. 请求消息
        List<ChatCompletionCommonRequest.Prompt> prompts = chatProcess.getMessages().stream()
                .map(entity -> ChatCompletionCommonRequest.Prompt.builder()
                        .role(com.jzy.chatglmsdk18753goldn.model.Role.user.getCode())
                        .content(entity.getContent())
                        .build())
                .collect(Collectors.toList());

        // 2. 封装参数
        QwenChatCompletionRequest request = null;
        try {
            request = new QwenChatCompletionRequest();
            request.setModel(com.jzy.chatglmsdk18753goldn.model.Model.valueOf(ChatModel.get(chatProcess.getModel()).name())); // chatGLM_6b_SSE、chatglm_lite、chatglm_lite_32k、chatglm_std、chatglm_pro
            request.setMessages(prompts);
            request.setEnableThinking(false);
        } catch (IllegalArgumentException e) {
            log.error("模型参数异常", e);
            throw new RuntimeException(e);
        }

        long startTime = System.currentTimeMillis();
        qwenOpenAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);

                // 发送信息
                if (EventType.add.getCode().equals(type)) {
                    // Prometheus 埋点
                    prometheusCollectionThreadPoolExecutor.submit(() ->{
                        try {
                            if (firstTokenTimeMap.putIfAbsent(chatProcess.getSessionId(), System.currentTimeMillis()) == null) {
                                // 记录首字延迟
                                Timer.builder(PrometheusCollectionConstants.CHAT_FIRST_TOKEN_LATENCY)
                                        .tags("model", chatProcess.getModel() == null ? "null" : chatProcess.getModel())
                                        .register(registry)
                                        .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
                            }
                        } catch (Exception e) {
                            log.error("首字延迟埋点异常", e);
                        }
                    });
                    try {
                        emitter.send(response.getData());
                    } catch (Exception e) {
                        throw new ChatGPTException(e.getMessage());
                    }
                }

                // type 消息类型，add 增量，finish 结束，error 错误，interrupted 中断
                if (EventType.finish.getCode().equals(type)) {
                    ChatCompletionResponse.Meta meta = JSON.parseObject(response.getMeta(), ChatCompletionResponse.Meta.class);
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(meta));
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                emitter.complete();
                // Prometheus 埋点
                prometheusCollectionThreadPoolExecutor.submit(() -> {
                    try {
                        firstTokenTimeMap.remove(chatProcess.getSessionId());
                        String[] tags = new String[]{
                                "model", chatProcess.getModel() == null ? "null" : chatProcess.getModel(),
                        };
                        // 次数统计
                        Counter counter = Counter.builder(PrometheusCollectionConstants.CHAT_COMPLETIONS_TOTAL)
                                .tags(tags)
                                .register(registry);
                        counter.increment();

                        // 接口耗时统计（修正计算方式）
                        Timer.builder(PrometheusCollectionConstants.CHAT_COMPLETIONS_DURATION)
                                .tags(tags)
                                .register(registry)
                                .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("会话统计埋点异常", e);
                    }
                });


            }

        });

    }

}
