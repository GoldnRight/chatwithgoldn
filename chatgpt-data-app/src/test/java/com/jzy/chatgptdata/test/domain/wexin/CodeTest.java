package com.jzy.chatgptdata.test.domain.wexin;

import com.jzy.chatgptdata.app.Application;
import com.jzy.chatgptdata.domain.auth.respository.IAuthRepository;
import com.jzy.chatgptdata.domain.weixin.respository.IWeiXinRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CodeTest {
    @Resource
    private IWeiXinRepository weiXinRepository;

    @Resource
    private IAuthRepository authRepository;

    String openId = "test_123456";


    @Test
    public void getCodeTest() {
        log.info("生成验证码 {}", weiXinRepository.genCode(openId));
    }

    @Test
    public void authCodeTest() throws InterruptedException {
        String code = authRepository.getCodeByOpenId(openId);
        String openId = authRepository.getCodeUserOpenId(code);
        log.info("验证码 {} 对应的 openId {}", code, openId);
        Thread.sleep(2000);
        authRepository.removeCodeByOpenId(code, openId);
        code = authRepository.getCodeByOpenId(openId);
        openId = authRepository.getCodeUserOpenId(code);
        if(code == null && openId == null){
            log.info("验证码已过期");
        }

    }

}
