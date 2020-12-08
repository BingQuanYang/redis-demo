package com.smart.redis.service.impl;

import com.smart.redis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Slf4j
class RedisServiceImplTest {
    @Resource
    ValueOperations<String, Object> valueOperations;
    @Resource
    ListOperations<String, Object> listOperations;
    @Resource
    HashOperations<String, String, Object> hashOperations;

    @Test
    public void testString() {
        //set
        valueOperations.set("username", "admin");
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("13456");
        valueOperations.set("cache:user", admin);
        User zs = new User();
        zs.setUsername("zs");
        zs.setPassword("111");
        User ls = new User();
        ls.setUsername("ls");
        ls.setPassword("222");
        ArrayList<User> users = new ArrayList<>();
        users.add(admin);
        users.add(zs);
        users.add(ls);
        valueOperations.set("cache:users", users);

        //如果key不存在 才去设置值 否则不设置
        valueOperations.setIfAbsent("nx", "nxnxnx", Duration.ofSeconds(60));
        //如果key存在去设置值 否则不设置值
        valueOperations.setIfPresent("nx", "nxnxnx", Duration.ofSeconds(120));
        valueOperations.setIfPresent("nx1", "nxnxnx", Duration.ofSeconds(120));


        //计数
        //加2
        valueOperations.increment("valueIncr", 2);
        //加1
        valueOperations.increment("valueIncr");
        valueOperations.increment("valueIncr");
        //减1
        valueOperations.decrement("valueDecr", 1);
        //减1
        valueOperations.decrement("valueDecr");
        valueOperations.decrement("valueDecr");
    }


    @Test
    public void testList() {
        //在列表的最左边塞入一个value
        listOperations.leftPush("test:list", "1");
        listOperations.leftPush("test:list", "11");

        //获取指定索引位置的值, index为-1时，表示返回的是最后一个；当index大于实际的列表长度时，返回null
        String index = (String) listOperations.index("test:list", 0);
        log.info(index);

        /**
         * 获取范围值，闭区间，start和end这两个下标的值都会返回; end为-1时，表示获取的是最后一个；
         * 如果希望返回最后两个元素，可以传入  -2, -1
         */
        List<Object> list = listOperations.range("test:list", -2, -1);
        log.info(list.toString());

        //获取列表长度
        Long size = listOperations.size("test:list");
        log.info(size.toString());

        //设置list中指定下标的值，采用干的是替换规则, 最左边的下标为0；-1表示最右边的一个
        listOperations.set("test:list", -1, "666");

        /**
         * 删除列表中值为value的元素，总共删除count次；
         * 如原来列表为 【1， 2， 3， 4， 5， 2， 1， 2， 5】
         * 传入参数 count=1 ,value=2表示删除一个列表中value为2的元素
         * 则执行后，列表为 【1， 3， 4， 5， 2， 1， 2， 5】
         */
        listOperations.remove("test:list", 1, "11");

        //删除list首尾，只保留 [start, end] 之间的值
        //listOperations.trim(start,end);
    }


    @Test
    public void testHash() {
        //添加or更新hash的值
        hashOperations.put("test:map", "username", "admin");

        //获取hash中field对应的值
        String username = (String) hashOperations.get("test:map", "username");
        log.info(username);

        //删除hash中field这一对kv
        hashOperations.delete("test:map", "username");
    }

}