package com.jzy.chatgptdata.domain.openai.repository;

import com.jzy.chatgptdata.domain.openai.model.entity.UserAccountQuotaEntity;
import org.springframework.stereotype.Repository;

/**
 * @description OpenAi 仓储接口
 */
@Repository
public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);

}
