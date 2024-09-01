package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserLoginMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserLoginMapper userLoginMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    //微信用户登录
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) throws IOException {
        //获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空则判断登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //获取用户，并判断用户是否为空，从而判断是否为新用户
        User user = userLoginMapper.getByOpenid(openid);
        //如果是新用户，要插入数据库
        if(user == null){
             user = User.builder()
                    .createTime(LocalDateTime.now())
                    .openid(openid)
                    .build();
            userLoginMapper.insert(user);
        }
        return user;
    }

    //获取openid
    private String getOpenid(String code) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }


}
