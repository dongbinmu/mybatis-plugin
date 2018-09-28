package com.weaponlin;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringTest {

    @Test
    public void index_test() {
        String sql = "select *    from  demo.user where F_user_id = 10";
        int index = sql.indexOf(" from ");
        String trim = sql.substring(index + 6).trim();
        int spaceIndex = trim.indexOf(" ");
        String db = "";
        if (spaceIndex != -1) {
            db = trim.substring(0, spaceIndex);
        }
        Assert.assertEquals(db, "demo.user");
    }

    @Test
    public void replace_test() {
        String a = "aabb";
        String b = a.replace("aa", "bb");
        Assert.assertEquals("bbbb", b);
    }

    @Test
    public void sql_replace_test() {
        String sql = "insert into demo.user(id, name, gender, age) values(?, ?, ?, ?)";

    }
}
