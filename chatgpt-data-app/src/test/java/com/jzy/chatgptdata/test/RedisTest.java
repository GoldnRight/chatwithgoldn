package com.jzy.chatgptdata.test;

import com.jzy.chatgptdata.app.Application;
import com.jzy.chatgptdata.infrastructure.redis.IRedisService;
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
    private IRedisService redisService;

    private String key = "test";

//    @Before
//    public void setUp() {
//        assertNotNull("RedissonService should not be null", redissonService);
//        // 可以通过反射检查 redissonClient 是否注入
//        try {
//            Field field = RedissonService.class.getDeclaredField("redissonClient");
//            field.setAccessible(true);
//            Object redissonClient = field.get(redissonService);
//            assertNotNull("RedissonClient should not be null", redissonClient);
//        } catch (Exception e) {
//            log.error("Failed to check redissonClient", e);
//        }
//    }
    @Test
    public void test() {
        redisService.setValue(key, "hello world", 10000);
        String value = redisService.getValue(key);
        log.info("value: {}", value);
    }

}
