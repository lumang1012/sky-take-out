package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserLoginMapper {

    //通过openid获得用户
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User getByOpenid(String openid);

    //插入新用户
    void insert(User user);
}
