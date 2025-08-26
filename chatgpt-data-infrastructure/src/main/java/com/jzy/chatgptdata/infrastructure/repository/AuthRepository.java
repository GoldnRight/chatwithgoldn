package com.jzy.chatgptdata.infrastructure.repository;

import com.jzy.chatgptdata.domain.auth.respository.IAuthRepository;
import com.jzy.chatgptdata.infrastructure.redis.IRedisService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AuthRepository implements IAuthRepository {
    private static final String Key = "weixin_code";

    @Resource
    private IRedisService redisService;

    @Override
    public String getCodeUserOpenId(String code) {
        return redisService.getValue(Key + "_" + code);
    }

    public String getCodeByOpenId(String openId) {
        return redisService.getValue(Key + "_" + openId);
    }

    @Override
    public void removeCodeByOpenId(String code, String openId) {
        redisService.remove(Key + "_" + code);
        redisService.remove(Key + "_" + openId);
    }

}
