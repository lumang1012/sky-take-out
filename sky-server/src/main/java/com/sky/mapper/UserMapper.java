package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {



    //通过openid获得用户
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User getByOpenid(String openid);

    //插入新用户
    void insert(User user);

    //根据时间统计用户数量
    Double countByMap(Map map);
}
