package com.jzy.chatgptdata.domain.weixin.respository;

/**
 * @description 微信服务仓储
 */
public interface IWeiXinRepository {

    String genCode(String openId);

}
