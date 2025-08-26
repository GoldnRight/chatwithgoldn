package com.jzy.chatgptdata.domain.auth.respository;

/**
 * @description 认证仓储服务
 */
public interface IAuthRepository {

    String getCodeUserOpenId(String code);

    String getCodeByOpenId(String openId);

    void removeCodeByOpenId(String code, String openId);

}

