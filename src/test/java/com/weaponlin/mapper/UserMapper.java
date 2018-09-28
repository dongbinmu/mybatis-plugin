package com.weaponlin.mapper;

import com.weaponlin.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    List<User> select(@Param("hash") long hash);

    int insert(@Param("hash") long hash, @Param("user") User user);
}
