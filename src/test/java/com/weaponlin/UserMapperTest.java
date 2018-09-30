package com.weaponlin;

import com.weaponlin.entity.User;
import com.weaponlin.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
// 使用junit4进行测试
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void select_test() {
        List<User> list = userMapper.select(24234233000L);
        System.out.println(list);
    }

    @Test
    public void insert_test() {
        Long id = Long.valueOf(RandomStringUtils.randomNumeric(8) + "000");
        User user = new User().setId(id).setName("weaponlin").setGender("male").setAge(23);
        int result = userMapper.insert(id, user);
        assertEquals(1, result);
    }
}
