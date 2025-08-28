package com.jzy.chatgptdata.test;

import com.jzy.chatgptdata.app.Application;
import com.jzy.chatgptdata.domain.auth.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisTest {
    @Resource
    private IAuthService authService;

    private String key = "test";



}
