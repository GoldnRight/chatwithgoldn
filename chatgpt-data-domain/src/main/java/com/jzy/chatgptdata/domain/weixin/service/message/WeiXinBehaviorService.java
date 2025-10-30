package com.jzy.chatgptdata.domain.weixin.service.message;

import com.jzy.chatgptdata.domain.weixin.model.entity.MessageTextEntity;
import com.jzy.chatgptdata.domain.weixin.model.entity.UserBehaviorMessageEntity;
import com.jzy.chatgptdata.domain.weixin.model.entity.WxConfigEntity;
import com.jzy.chatgptdata.domain.weixin.model.valobj.MsgTypeVO;
import com.jzy.chatgptdata.domain.weixin.respository.IWeiXinRepository;
import com.jzy.chatgptdata.domain.weixin.service.IWeiXinBehaviorService;
import com.jzy.chatgptdata.types.exception.ChatGPTException;
import com.jzy.chatgptdata.types.sdk.weixin.XmlUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 受理用户行为接口实现类
 */
@Service
public class WeiXinBehaviorService implements IWeiXinBehaviorService {


    @Resource
    private WxConfigEntity wxConfig;

    @Resource
    private IWeiXinRepository repository;

    /**
     * 1. 用户的请求行文，分为事件event、消息text，这里我们只处理消息内容
     * 2. 用户行为、消息类型，是多样性的，这部分如果用户有更多的扩展需求，可以使用设计模式【模板模式 + 策略模式 + 工厂模式】，分拆逻辑。
     */
    @Override
    public String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity) {
        // Event 事件类型，忽略处理
        if (MsgTypeVO.EVENT.getCode().equals(userBehaviorMessageEntity.getMsgType())) {
            return "";
        }

        // Text 文本类型
        if (MsgTypeVO.TEXT.getCode().equals(userBehaviorMessageEntity.getMsgType()) && "403".equals(userBehaviorMessageEntity.getContent())) {

            String code = repository.genCode(userBehaviorMessageEntity.getOpenId());

            // 反馈信息[文本]
            MessageTextEntity res = new MessageTextEntity();
            res.setToUserName(userBehaviorMessageEntity.getOpenId());
            res.setFromUserName(wxConfig.getOriginalId());
            res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
            res.setMsgType("text");
            res.setContent(String.format("您的验证码为：%s 有效期%d分钟！", code, 3));
            return XmlUtil.beanToXml(res);
        }

        if (MsgTypeVO.TEXT.getCode().equals(userBehaviorMessageEntity.getMsgType())) {

            // 反馈信息[文本]
            MessageTextEntity res = new MessageTextEntity();
            res.setToUserName(userBehaviorMessageEntity.getOpenId());
            res.setFromUserName(wxConfig.getOriginalId());
            res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
            res.setMsgType("text");
            res.setContent(String.format("请输入【403】获取登录验证码"));
            return XmlUtil.beanToXml(res);
        }

        throw new ChatGPTException(userBehaviorMessageEntity.getMsgType() + " 未被处理的行为类型 Err！");
    }

}
