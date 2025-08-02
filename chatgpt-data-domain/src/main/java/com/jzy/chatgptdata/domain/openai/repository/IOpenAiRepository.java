package com.jzy.chatgptdata.domain.openai.repository;

import com.jzy.chatgptdata.domain.openai.model.entity.UserAccountQuotaEntity;
import org.springframework.stereotype.Repository;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description OpenAi 仓储接口
 * @create 2023-10-03 16:49
 */
@Repository
public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);

}
