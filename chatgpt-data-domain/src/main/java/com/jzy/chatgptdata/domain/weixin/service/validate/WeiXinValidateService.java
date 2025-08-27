package com.jzy.chatgptdata.domain.weixin.service.validate;

import com.jzy.chatgptdata.domain.weixin.model.entity.WxConfigEntity;
import com.jzy.chatgptdata.domain.weixin.service.IWeiXinValidateService;
import com.jzy.chatgptdata.types.sdk.weixin.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 验签接口实现
 */
@Service
public class WeiXinValidateService implements IWeiXinValidateService {

//    @Value("${wx.config.token}")
//    private String token;

    @Resource
    private WxConfigEntity wxConfig;

    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) {
        return SignatureUtil.check(wxConfig.getToken(), signature, timestamp, nonce);
    }

}
