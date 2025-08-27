package com.jzy.chatgptdata.domain.weixin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "wx.config", ignoreInvalidFields = true)
@Component
@NoArgsConstructor
@AllArgsConstructor
public class WxConfigEntity {
    // 公众号原始id
    private String originalId;
    // 微信公众号appid
    private String appid;
    // 微信公众号token
    private String token;
    // 微信公众号EncodingAesKey
    private String defaultBase64EncodedSecretKey;
}
