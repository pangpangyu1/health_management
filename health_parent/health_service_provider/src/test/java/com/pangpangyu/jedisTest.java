package com.pangpangyu;

import com.pangpangyu.constant.RedisConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class jedisTest {
    @Test
    public void diffTest(){
        JedisPool jedisPool = new JedisPool();
        Set<String> smembers = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_DB_RESOURCES,RedisConstant.SETMEAL_PIC_RESOURCES);
        for (String smember : smembers) {
            System.out.println(smember);
        }
    }
}
